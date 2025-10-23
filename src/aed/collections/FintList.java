package aed.collections;

import aed.utils.TimeAnalysisUtils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;


/*
TODO:

Este código esta enorme hahaah, que eu estou me a passar haha
 */

public class FintList implements Iterable<Integer> {

    public class FintListIterator implements Iterator<Integer> {

        private int index = head;

        @Override
        public boolean hasNext() {
            return index != -1;
        }

        @Override
        public Integer next() {

            int result = values[index];
            index = nextIndex[index];

            return result;
        }
    }


    // todo---------------------------------------------------------

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


    public FintList()
    {
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


    public boolean add(int item)
    {

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


    public int get()
    {
        if (isEmpty()){
            throw new IndexOutOfBoundsException("Erro no get");
        }
        return values[tail];
    }

    public boolean isEmpty() { return size == 0; }

    public int size() { return size; }

    public int remove()
    {
        if (isEmpty()){
            throw new IndexOutOfBoundsException("Erro no remove");
        }

        int index = tail;

        if (size == 1){

            index = head;
            head = -1;
            tail = -1;

        }else{

            int pneulIndex = prevIndex[tail];;
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


    public void addAt(int index, int item)
    {
        if (index < 0 || index > size()){
            throw new IndexOutOfBoundsException("Erro no get");
        }

        if (index == size){
            add(item);
            return;
        }

        if (freeList == -1){
            resize(2*capacity);
        }


        int in = freeList;
        freeList = nextIndex[freeList];


        values[in] = item;

        if (index == 0){

            prevIndex[in] = -1;
            nextIndex[in] = head;

            if (head != -1){

                prevIndex[head] = in;
            }

            head = in;

            if (isEmpty()){
                tail = head;
            }

            lastIndex = index;
            lastNodeIndex = in;
            size++;

        }else{


            int next_index = head;
            int dista_head = index;
            int dista_tail = size-1-index;
            int dista_lastIndex = lastIndex - index;



            if (dista_lastIndex < 0){
                dista_lastIndex = dista_lastIndex * -1;
            }


            if (dista_head <= dista_lastIndex && dista_head <= dista_tail){
                next_index = head;
                for (int i = 0; i < index; i++){
                    next_index = nextIndex[next_index];
                }
            }


            else if (dista_tail < dista_lastIndex){
                next_index = tail;
                for (int i = size - 1; i > index; i--){
                    next_index = prevIndex[next_index];
                }
            }


            else{

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

            int prev = prevIndex[next_index];

            nextIndex[in] = next_index;
            prevIndex[in] = prev;

            if (prev != -1){
                nextIndex[prev] = in;
            }

            prevIndex[next_index] = in;

            size++;
            lastNodeIndex = in;
            lastIndex = index;
        }
    }

    public int getFirst()
    {
        if (isEmpty()){
            throw new IndexOutOfBoundsException("Erro no getFirst");
        }

        return values[head];
    }

    public int get(int index)
    {

        if (index < 0 || index >= size()){
            throw new IndexOutOfBoundsException("Erro no get");
        }

        if (lastIndex != -1){
            if(index == lastIndex){
                //System.out.println("Passei por aqui 1");

                return values[lastNodeIndex];

            } else if (index == lastIndex + 1){
                //System.out.println("Passei por aqui 2");

                int x = nextIndex[lastNodeIndex];
                lastIndex = index;
                lastNodeIndex = x;

                if (x != -1){
                    return values[x];
                }

            }else if (index == lastIndex - 1){
                //System.out.println("Passei por aqui 3");


                int x = prevIndex[lastNodeIndex];

                lastNodeIndex = x;
                lastIndex = index;

                if (x != -1){
                    return values[x];
                }
            }
        }

        int dista_head = index;
        int dista_tail = size-1-index;
        int dista_lastIndex = lastIndex - index;

        if (dista_lastIndex < 0){
            dista_lastIndex = dista_lastIndex * -1;
        }

        int index_atual;

        if (dista_head < dista_lastIndex && dista_head < dista_tail){
            index_atual = head;
            for (int i = 0; i < index; i++){
                index_atual = nextIndex[index_atual];
            }
        }
        else if (dista_tail < dista_lastIndex){
            index_atual = tail;
            for (int i = size - 1; i > index; i--){
                index_atual = prevIndex[index_atual];
            }
        }else{

            if (index > lastIndex){
                index_atual = lastNodeIndex;
                for (int i = lastIndex; i < index; i++){
                    index_atual = nextIndex[index_atual];
                }
            }else{
                index_atual = lastNodeIndex;
                for (int i = lastIndex; i > index; i--){
                    index_atual = prevIndex[index_atual];
                }

            }
        }


        lastIndex = index;
        lastNodeIndex = index_atual;

        return values[index_atual];
    }

    public void set(int index, int item)
    {

        if (index < 0 || index >= size()){
            throw new IndexOutOfBoundsException("Erro no set");
        }

        if (lastIndex != -1){

            if(index == lastIndex){
                //System.out.println("Set -> Passei por aqui 1");
                values[lastNodeIndex] = item;
                return;

            } else if (index == lastIndex + 1){


                //System.out.println("Set -> Passei por aqui 2");

                int x = nextIndex[lastNodeIndex];
                if (x != -1){

                    values[x] = item;
                }
                lastIndex = index;
                lastNodeIndex = x;
                return;

            }else if (index == lastIndex - 1){

                //System.out.println("Set -> Passei por aqui 3");

                int x = prevIndex[lastNodeIndex];

                if (x != -1){
                    values[x] = item;
                }

                lastIndex = index;
                lastNodeIndex = x;
                return;
            }
        }


        int index_atual = head;
        for (int i = 0; i < index; i++){
            index_atual = nextIndex[index_atual];
        }
        //System.out.println("Set -> Passei por aqui 4");


        values[index_atual] = item;
        lastIndex = index;
        lastNodeIndex = index_atual;
    }

    public int removeAt(int index) {

        if (index < 0 || index >= size()){
            throw new IndexOutOfBoundsException("Erro no removeAt" + index);
        }

        int value = 0;

        if (index == 0){
            int index_remover = head;

            if (index_remover == -1){
                throw new IndexOutOfBoundsException("erro no remveAt");
            }

            value = values[index_remover];
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

        }else if(index == size - 1){
            //System.out.println("RemoveAt ->Passei por aqui 4");
            return remove();

        }

        int index_remover = head;

        int dista_head = index;
        int dista_tail = size-1-index;
        int dista_lastIndex = lastIndex - index;

        if (lastNodeIndex == -1 || lastIndex == -1){
            dista_lastIndex = size();
        }



        if (dista_lastIndex < 0){
            dista_lastIndex = dista_lastIndex * -1;
        }


        if (dista_head <= dista_lastIndex && dista_head <= dista_tail ){
            index_remover = head;
            for (int i = 0; i < index; i++){
                index_remover = nextIndex[index_remover];
            }
        }
        else if (dista_tail < dista_lastIndex){
            index_remover = tail;
            for (int i = size - 1; i > index; i--){
                index_remover = prevIndex[index_remover];
            }
        }else{

            if (index > lastIndex){

                index_remover = lastNodeIndex;
                for (int i = lastIndex; i < index; i++){
                    index_remover = nextIndex[index_remover];
                }
            }else{

                //System.out.println("Passei por Aqui - RemoveAt - da direita para a esquerda");

                index_remover = lastNodeIndex;
                for (int i = lastIndex; i > index; i--){
                    index_remover = prevIndex[index_remover];
                }

            }
        }


        value = values[index_remover];

        int prev = prevIndex[index_remover];
        int next = nextIndex[index_remover];


        if (prev != -1){
            nextIndex[prev] = nextIndex[index_remover];
        }

        if (next != -1){
            prevIndex[next] = prevIndex[index_remover];
        }

        nextIndex[index_remover] = freeList;
        freeList = index_remover;
        size--;

        lastIndex = index;
        lastNodeIndex = next;



        return value;
    }

    public int indexOf(int item)
    {
        if (isEmpty()){
            return -1;
        }

        int index = head;
        for (int i = 0; i < size(); i++){

            if (index == -1){
                break;
            }


            if (values[index] == item){
                return i;
            }

            index = nextIndex[index];
        }

        return -1;
    }

    public boolean contains(int item)
    {

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
    public boolean remove(int item)
    {
        int index = indexOf(item);
        if (index == -1){
            return false;
        }

        removeAt(index);

        lastNodeIndex = -1;
        lastIndex = -1;

        return true;
    }

    public void reverse()
    {

        lastIndex = -1;
        lastNodeIndex = -1;

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


    public Iterator<Integer> iterator()
    {
        return new FintListIterator();
    }


    //a utilizacao de ? super Integer e por causa da implementacao da interface Iterable
    // https://zetcode.com/java/consumer/
    public void forEach(Consumer<? super Integer> c)
    {
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
    public int reduce(BinaryOperator<Integer> op, int defaultValue)
    {
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

    public void print() {
        if (isEmpty()) {
            return;
        }

        int index = head;

        while (index != -1) {
            System.out.print(values[index] + " ");
            index = nextIndex[index];
        }
        System.out.println();
    }

    public static void main(String[] args)
    {
        FintList f = new FintList();

        for (int i = 0; i < 2000000; i++) {
            f.addAt(i, i);
        }


        long avgTime = TimeAnalysisUtils.getAverageCPUTime(() -> {
        });

        System.out.println("Tempo médio de get(i): " + avgTime / 1E6 + " ms");

        avgTime = TimeAnalysisUtils.getAverageCPUTime(() -> {
            FintList f_para_testar = f.deepCopy();

            for (int i = f.size() - 1; i >= 1000000; i--) {
                f_para_testar.removeAt(i);
            }
        });

        System.out.println("Tempo médio de removeAt: " + avgTime / 1E6 + " ms");



        Consumer<Integer> a = p -> {
            System.out.println(p*3);
        };

        UnaryOperator<Integer> b = c -> c * 4;

        BinaryOperator<Integer> soma = (x,y) -> x+y;
        BinaryOperator<Integer> mult = (x,y) -> x*y;
    }
}
