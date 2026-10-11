public class UndoStack {
    private Stroke[] items = new Stroke[3];
    private int start = 0;
    private int size =0;
    
    public void push (Stroke stroke){
          System.out.println("UndoStack push called");
        if(size<items.length){
             items[(start + size) % items.length] = stroke;
             //circular queue implemetation is done here
            size++;    
         }else{
            start=(start+1)%items.length;
             items[(start + size - 1) % items.length] = stroke;
         }
    }
    public Stroke pop(){
        if (size==0)
            return null;

        int index =( start+size-1)% items.length;
         Stroke stroke = items[index];
        items[index] = null;
        size--;

        return stroke;
    }
    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        for (int i = 0; i < items.length; i++) {
            items[i] = null;
        }

        start = 0;
        size = 0;
    }
}
