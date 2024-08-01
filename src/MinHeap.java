import java.util.ArrayList;

public class MinHeap {

   private final ArrayList<Node> heap;
   private int size;
   
   public MinHeap(){
      heap = new ArrayList<>();
      heap.add(null); // Add a dummy value at index 0
      size = 0;
   }

   public void add (Node v){
      int index = size + 1;         //index where we'll add the new value
      v.setIndex(index);            //set index of the node
      heap.add(v);              //add value at that position
            
      while ( index > 1 ) {                     //while our value has parents
         int parentIndex = index / 2;           //get parent index by dividing by 2
         if(heap.get(parentIndex).getF() > v.getF()) {            //if parent's value is higher
            heap.set(index, heap.get(parentIndex));    //perform swap
            heap.set(parentIndex, v);

            heap.get(index).setIndex(index);         //update index of the node
            heap.get(parentIndex).setIndex(parentIndex); //update index of the node
            
            index = parentIndex;                //update index after swap
         }else{                     //no swap needed         
            break;         
         }
      }
         
      size ++;                      //increase size
         
   }
   
   public Node remove (){
      if (size == 0) {              //if heap is empty
         return null;                 //return null
      }

      Node rootValue = heap.get(1);      //store root value to be returned at the end
      Node v = heap.get(size);           //grab value from last node in the heap
      v.setIndex(1);                 //set index of the node
      heap.set(1, v);                  //put value at the root
      heap.remove(size);               //deletes the last node in the heap
      int index = 1;                //index of value starts at root
      
      while (index * 2 < size) {    //while there are children of pos index
         int leftIndex = index * 2;
         int rightIndex = leftIndex + 1;
         
         Node leftChild = heap.get(leftIndex);
         Node rightChild = v;              //assign temp value to right child that would never cause a swap
                                          //we do this in case there's not a right child
                                    
         if(rightIndex < size){           //is there a right child
            rightChild = heap.get(rightIndex); //assign actual value of right child instead of v
         }
         
         Node minChild;
         int minIndex;
         
         if(leftChild.getF() < rightChild.getF()){      //determine the smaller of the two children
            minChild = leftChild;
            minIndex = leftIndex;
         }else{
            minChild = rightChild;
            minIndex = rightIndex;
         }
         
         if(minChild.getF() < v.getF()){                //is the smaller child smaller than our value -> swap
            heap.set(minIndex, v);           //swap value with smaller child
            heap.set(index, minChild);

            heap.get(minIndex).setIndex(minIndex);         //update index of the node
            heap.get(index).setIndex(index); //update index of the node

            index = minIndex;             //update index after swap
         }else{                           //no children are smaller -> stop
            break;
         }
      
      }
      
      size --;                      //update size
      return rootValue;             //return stored root value
   
   }

   public void reheapify() {
      int index = 1; // Start at the root
      while (true) {
         int leftIndex = 2 * index; // Index of the left child
         int rightIndex = 2 * index + 1; // Index of the right child
         int smallestIndex = index; // Start with the current index as the smallest

         // If the left child exists and is smaller than the current smallest, update smallestIndex
         if (leftIndex <= size && heap.get(leftIndex).getF() < heap.get(smallestIndex).getF()) {
            smallestIndex = leftIndex;
         }

         // If the right child exists and is smaller than the current smallest, update smallestIndex
         if (rightIndex <= size && heap.get(rightIndex).getF() < heap.get(smallestIndex).getF()) {
            smallestIndex = rightIndex;
         }

         // If the smallest index is not the current index, swap and continue
         if (smallestIndex != index) {
            // Swap the current node with the smaller child
            Node temp = heap.get(index);
            heap.set(index, heap.get(smallestIndex));
            heap.set(smallestIndex, temp);

            // Update the indices of the swapped nodes
            heap.get(index).setIndex(index);
            heap.get(smallestIndex).setIndex(smallestIndex);

            // Move the index to the smallest child's index and continue the loop
            index = smallestIndex;
         } else {
            // If no swaps are needed, the heap property is restored
            break;
         }
      }
   }

   public int getSize() {
      return size;
   }

   public boolean isEmpty(){
        return size == 0;
    }

   public boolean contains(Node node){
      for (int i = 1; i <= size; i++) {
         if(heap.get(i).equals(node)) {
            return true;
         }
      }
      return false;
   }

   public void printHeap() {
      for (int i = 1; i <= size; i++) {         //print all elements in the heap
         System.out.print(heap.get(i) + "\n");
      }
      System.out.println();
   }
}
