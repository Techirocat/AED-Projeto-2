package aed.collections;
import aed.utils.TemporalAnalysisUtils;
import aed.utils.TimeAnalysisUtils;


import java.util.Iterator;
import java.util.Random;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;


/*
TODO:

Este código esta enorme hahaah, que eu estou me a passar haha
 */

public class FintList implements Iterable<Integer> {

    public class FintListIterator implements Iterator<Integer> {
        private int index = head;

        public boolean hasNext() {
            return index != -1;
        }

        public Integer next() {
            int result = values[index];
            index = nextIndex[index];
            return result;
        }
    }


    private final int CAPACIDADE_INICIAL = 16;

    private int[] values;
    private int[] nextIndex;
    private int[] prevIndex;

    private int size;
    private int capacity;
    private int head;
    private int freeList;
    private int tail;
    private int lastIndex; // index da sequencia
    private int lastNodeIndex; // index do array


    public FintList() {
        values = new int[CAPACIDADE_INICIAL];
        nextIndex = new int[CAPACIDADE_INICIAL];
        prevIndex = new int[CAPACIDADE_INICIAL];

        size = 0;
        capacity = CAPACIDADE_INICIAL;
        head = -1;
        freeList = 0;
        tail = -1;
        lastIndex = -1;
        lastNodeIndex = -1;

        for (int i = 0; i < capacity; i++){
            prevIndex[i] = i-1;
            nextIndex[i] = i+1;

            if (i == capacity - 1){
                nextIndex[i] = -1;
                break;
            }
        }
    }

    public void resize(int c){

        int[] values = new int[c];
        int[] nextIndex = new int[c];
        int[] prevIndex = new int[c];


        for (int i = 0; i < capacity; i++){
            values[i] = this.values[i];
            nextIndex[i] = this.nextIndex[i];
            prevIndex[i] = this.prevIndex[i];
        }

        for (int i = capacity; i < c; i++){
            prevIndex[i] = i-1;
            nextIndex[i] = i+1;
            if (i == c-1){
                nextIndex[i] = -1;
            }
        }

        freeList = capacity;
        capacity = c;

        this.values = values;
        this.nextIndex = nextIndex;
        this.prevIndex = prevIndex;
    }


    public boolean add(int item) {

        if (freeList == -1){
            resize(capacity*2);
        }

        int index = freeList;
        freeList = nextIndex[freeList];

        values[index] = item;
        nextIndex[index] = -1;
        prevIndex[index] = tail;

        if (isEmpty()){
            head = index;
        }else{
            nextIndex[tail] = index;
        }

        tail = index;
        size++;

        return true;
    }


    public int get() {
        if (isEmpty()){
            throw new IndexOutOfBoundsException("Erro no get");
        }
        return values[tail];
    }

    public boolean isEmpty() { return size == 0; }

    public int size() { return size; }

    public int remove() {
        if (isEmpty()){
            throw new IndexOutOfBoundsException("Erro no remove");
        }

        int index = tail;

        if (size == 1){
            index = head;
            head = -1;
            tail = -1;

        }else{

            int pneulIndex = prevIndex[tail];
            nextIndex[pneulIndex] = -1;
            tail = pneulIndex;

        }

        nextIndex[index] = freeList;
        freeList = index;

        lastNodeIndex = -1;
        lastIndex = -1;

        size--;

        return values[index];
    }


    public void addAt(int index, int item) {
        if (index < 0 || index > size()){
            throw new IndexOutOfBoundsException("Erro no addAt");
        }

        if (index == size){
            add(item);
            return;
        }

        if (freeList == -1){
            resize(2*capacity);
        }


        int newIndex = freeList;
        freeList = nextIndex[newIndex];
        values[newIndex] = item;


        if (index == 0){

            prevIndex[newIndex] = -1;
            nextIndex[newIndex] = head;

            if (head != -1){
                prevIndex[head] = newIndex;
            }

            head = newIndex;

            if (isEmpty()){
                tail = head;
            }

            lastIndex = index;
            lastNodeIndex = newIndex;
            size++;
            return;
        }


        int next_index = getNextIndex(index);
        int prev = prevIndex[next_index];

        nextIndex[newIndex] = next_index;
        prevIndex[newIndex] = prev;

        if (prev != -1){
            nextIndex[prev] = newIndex;
        }

        prevIndex[next_index] = newIndex;

        size++;
        lastNodeIndex = newIndex;
        lastIndex = index;
        
    }

    private int getNextIndex(int index) {
        int next_index;
        int dista_tail = size - 1 - index;
        int dista_lastIndex = lastIndex - index;


        if (dista_lastIndex < 0){
            dista_lastIndex = dista_lastIndex * -1;
        }


        if (index <= dista_lastIndex && index <= dista_tail){
            next_index = head;
            for (int i = 0; i < index; i++){
                next_index = nextIndex[next_index];
            }
        } else if (dista_tail < dista_lastIndex){
            next_index = tail;
            for (int i = size - 1; i > index; i--){
                next_index = prevIndex[next_index];
            }
        } else{

            if (index > lastIndex){
                next_index = lastNodeIndex;
                for (int i = lastIndex; i < index; i++){
                    next_index = nextIndex[next_index];
                }
            }else{
                next_index = lastNodeIndex;
                for (int i = lastIndex; i > index; i--){
                    next_index = prevIndex[next_index];
                }

            }
        }
        return next_index;
    }

    public int getFirst() {
        if (isEmpty()){
            throw new IndexOutOfBoundsException("Erro no getFirst");
        }

        return values[head];
    }

    public int get(int index) {
        if (index < 0 || index >= size()){
            throw new IndexOutOfBoundsException("Erro no get");
        }


        int index_atual = getNextIndex(index);
        lastIndex = index;
        lastNodeIndex = index_atual;

        return values[index_atual];
    }

    public void set(int index, int item) {
        if (index < 0 || index >= size()){
            throw new IndexOutOfBoundsException("Erro no set");
        }

        int index_atual = getNextIndex(index);

        values[index_atual] = item;
        lastIndex = index;
        lastNodeIndex = index_atual;
    }

    public int removeAt(int index) {
        if (index < 0 || index >= size()){
            throw new IndexOutOfBoundsException("Erro no removeAt" + index);
        }


        if (index == 0){
            int index_remover = head;

            if (index_remover == -1){
                throw new IndexOutOfBoundsException("erro no remveAt");
            }

            int value = values[index_remover];
            head = nextIndex[index_remover];

            if (head != -1){
                prevIndex[head] = -1;
            }

            size--;

            if (head == -1){
                tail = -1;
            }

            nextIndex[index_remover] = freeList;
            freeList = index_remover;

            lastIndex = index;
            lastNodeIndex = head;

            return value;

        }

        if(index == size - 1){
            //System.out.println("RemoveAt ->Passei por aqui 4");
            return remove();

        }


        int index_remover = getNextIndex(index);

        int value = values[index_remover];
        int prev = prevIndex[index_remover];
        int next = nextIndex[index_remover];


        nextIndex[prev] = nextIndex[index_remover]; // não precisamos vereeficar que prev != -1 pois isso apenas acontece no caso index == 0, oq ja foi tratado
        prevIndex[next] = prevIndex[index_remover]; // o mesmo para next mas neste caso tratamos no index == size-1

        nextIndex[index_remover] = freeList;
        freeList = index_remover;
        size--;

        lastIndex = index;
        lastNodeIndex = next;

        return value;
    }

    public int indexOf(int item) {
        if (isEmpty()){
            return -1;
        }

        int index = head;

        for (int i = 0; i < size(); i++){
            int nodeValue = values[index];
            if(nodeValue == item){
                return i;
            }
            index = nextIndex[index];
        }
        return -1;
    }

    public boolean contains(int item) {
        if (isEmpty()){
            return false;
        }

        int index = head;

        while (index != -1){
            if (values[index] == item){
                return true;
            }
            index = nextIndex[index];
        }

        return false;
    }

    //este método não precisa de ser eficiente
    public boolean remove(int item) {
        int index = indexOf(item);

        if (index == -1){
            return false;
        }

        removeAt(index);
        return true;
    }

    public void reverse() {
        if (isEmpty()){
            return;
        }

        int index = head;

        while(index != -1){
            int prev = prevIndex[index];
            int next = nextIndex[index];

            nextIndex[index] = prev;
            prevIndex[index] = next;

            index = next;
        }

        int temp = head;
        head = tail;
        tail = temp;

        lastIndex = -1;
        lastNodeIndex = -1;
    }

    public FintList deepCopy()
    {
        FintList f = new FintList();
        f.values = new int[this.capacity];
        f.prevIndex = new int[this.capacity];
        f.nextIndex = new int[this.capacity];
        f.size = size;
        f.capacity = capacity;
        f.head = head;
        f.freeList = freeList;
        f.tail = tail;

        for (int i = 0; i < capacity; i++){
            f.values[i] = this.values[i];
            f.prevIndex[i] = this.prevIndex[i];
            f.nextIndex[i] = this.nextIndex[i];
        }

        lastIndex = -1;
        lastNodeIndex = -1;

        return f;
    }


    public Iterator<Integer> iterator() { return new FintListIterator(); }


    //a utilizacao de ? super Integer e por causa da implementacao da interface Iterable
    // https://zetcode.com/java/consumer/
    public void forEach(Consumer<? super Integer> c) {
        if (isEmpty()){
            return;
        }

        int index = head;

        while (index != -1){
            c.accept(values[index]);
            index = nextIndex[index];
        }
    }

    //https://www.geeksforgeeks.org/java/unaryoperator-interface-in-java/
    public void map(UnaryOperator<Integer> op) {
        if (isEmpty()) {
            return;
        }

        int index = head;

        while (index != -1){
            values[index] = op.apply(values[index]);
            index = nextIndex[index];
        }
    }

    //https://samedesilva.medium.com/an-easy-way-to-understand-binaryoperator-functional-interface-in-java8-deabe9f04370
    public int reduce(BinaryOperator<Integer> op, int defaultValue) {
        int result = defaultValue;

        if (head == -1 || isEmpty()){
            return result;
        }

        int index = head;
        while (index != -1){
            result = op.apply(result, values[index]);
            index = nextIndex[index];

        }

        return result;
    }


    public static void main(String[] args)
    {
        //LinkedList<Integer> l = new LinkedList<>();
        //FintList f = new FintList();
        //Consumer<Integer> print = c -> System.out.print(c + " ");
        Random random = new Random();


        Function<Integer, FintList> FinListGenerator = n -> {
            FintList list = new FintList();
            for (int i = 0; i < n; i++) {
                list.add(i);
            }
            return list;
        };

        Function<Integer, LinkedList<Integer>> LinkedListGenerator = n -> {
            LinkedList<Integer> list = new LinkedList<>();
            for (int i = 0; i < n; i++) {
                list.add(i);
            }
            return list;
        };

        //https://www.geeksforgeeks.org/java/function-interface-in-java/
        //public static<T> void runDoublingRatioTest(Function<Integer,T> exampleGenerator, Consumer<T> methodToTest, int iterations)




        //  TESTES PARA O MÉTODO ADDAT()
        System.out.println("#################### Método addAt() ###############################");

        Consumer<FintList> FTestAddAt = list -> {
            int n = list.size();
            for (int i = 0; i < n; i++){
                list.addAt(i, i);
            }
        };
        Consumer<LinkedList<Integer>> LTestAddAt = list -> {
            int n = list.size();
            for (int i = 0; i < n; i++){
                list.addAt(i, i);
            }
        };

        System.out.println("==== FinList ====");
        //TemporalAnalysisUtils.runDoublingRatioTest(FinListGenerator, FTestAddAt, 18);
        System.out.println("==== LinkedList ====");
        //TemporalAnalysisUtils.runDoublingRatioTest(LinkedListGenerator, LTestAddAt, 9);



        //  TESTES PARA O MÉTODO REMOVEAT()
        System.out.println("#################### Método RemoveAt ###############################");

        Consumer<FintList> FTestRemoveAt = list -> {
            int n = list.size();
            for (int i = n - 1; i >= 0; i--){
                list.removeAt(i);
            }
        };

        Consumer<LinkedList<Integer>> LTestRemoveAt = list -> {
            int n = list.size();
            for (int i = n - 1; i >= 0; i--){
                list.removeAt(i);
            }
        };


        System.out.println("==== FinList ====");
       // TemporalAnalysisUtils.runDoublingRatioTest(FinListGenerator, FTestRemoveAt, 18);
        System.out.println("==== LinkedList ====");
        //TimeAnalysisUtils.runDoublingRatioTest(LinkedListGenerator, LTestRemoveAt, 9);




        //  TESTES PARA O MÉTODO DEEPCOPY()

        System.out.println("#################### Método DeepCopy ###############################");


        Consumer<FintList> FTestDeepCopy = list -> {
            FintList fintLististCopy = list.deepCopy();
        };

        Consumer<LinkedList<Integer>> LTestDeepCopy = list -> {
            for (int i = 0; i < 1; i++){
                LinkedList<Integer> linkedListCopy = list.shallowCopy();
            }
        };

        System.out.println("==== FinList ====");
        //TemporalAnalysisUtils.runDoublingRatioTest(FinListGenerator, FTestDeepCopy, 18);
        System.out.println("==== LinkedList ====");
        TemporalAnalysisUtils.runDoublingRatioTest(LinkedListGenerator, LTestDeepCopy, 9);






    }
}
