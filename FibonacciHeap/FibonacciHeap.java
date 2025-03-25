
/**
	 * FibonacciHeap
	 *
	 * An implementation of a Fibonacci Heap over integers.
	 */
	public class FibonacciHeap{
		private HeapNode min;
		private int size;
		private int trees_amount;
		private HeapNode first;	
		private HeapNode last;
		private int marked_amount;
		private static int links=0;
		private static int cuts=0;
		
		/**
		 * FibonacciHeap constructor
		 * Complexity: O(1)
		 */
		public FibonacciHeap() {

			this.min=null;
			this.size=0;
			this.trees_amount=0;
			this.first=null;
			this.last=null;
			this.marked_amount=0;
		}

	   /**
	    * public boolean isEmpty()
	    * Returns true if and only if the heap is empty.
	    * Complexity: O(1)
	    */
	    public boolean isEmpty(){
	    	if (this.size==0) {return true;}{return false;}
	    }
			
	   /**
	    * public HeapNode insert(int key)
	    *
	    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
	    * The added key is assumed not to already belong to the heap.  
	    * 
	    * Returns the newly created node.
	    * Complexity: O(1)
	    */
	    public HeapNode insert(int key){   
	    	HeapNode node = new HeapNode(key);;
	    	if (this.size==0) {
	    		this.min=node;
	    		this.first=node;
	    		this.last=node;
	    	}
	    	else {
	    		if (node.key<min.key) {min=node;}
	    		HeapNode old_first = first;
	    		old_first.prev=node;
	    		first=node;
	    		first.next=old_first;
	    		first.prev=last;
	    		last.next=first;
	    		
	    	}
	    	this.size++;
	    	this.trees_amount++;
	    	return node; 
	    }
	    

	   /**
	    * public void deleteMin()
	    *
	    * Deletes the node containing the minimum key.
	    * Complexity: Worst case-O(n),  Amortized- O(logn)
	    */
	    public void deleteMin(){
	    	if(size == 0) { //no elements in the heap
	    		return;
	    	}
	    	HeapNode x =null; //for consolidate
	    	if(min.child != null) { //since all child are root for now, make them un-marked
	    		HeapNode first_child =min.child;
	    		if(first_child.mark) {
	    			first_child.mark=false;
	    			marked_amount--;
	    		}
	    		first_child.parent = null;
	    		HeapNode bro = first_child.next;
	    		while(bro != first_child ) {
	    			if(bro.mark) {
	    				bro.mark = false;
	    				marked_amount--;
	    			}
	    			bro.parent = null;
	    			bro = bro.next;
	    		}
	    	}
	    	if(min.prev == min) { //only 1 tree in the heap
	    		if(min.child==null) {
	    			first=null;
	    			last=null;
	    		}else {
	    			first=min.child;
	    			last=min.child.prev;
	    			x=min.child; //for consolidate
	    		}
	    	}else{ //more than 1 tree
	    		if(min.child == null) {
	    			min.prev.next = min.next;
	    			min.next.prev = min.prev;
	    			x=min.next; //for consolidate
	    		}
	    		else{ 
	    			HeapNode prevRoot = min.prev;
	    			HeapNode nextRoot = min.next;
	    			HeapNode firstChild = min.child;
	    			HeapNode lastChild = min.child.prev;
	    			firstChild.prev = prevRoot;
	    			prevRoot.next= firstChild;
	    			lastChild.next=nextRoot;
	    			nextRoot.prev=lastChild;
	    			x= firstChild; //for consolidate
	    		}
	    	}
	    	
	    	size--;
	    	trees_amount--;//in case min was the only node
	    	min.next = null;
	    	min.prev = null;
	    	min.child = null;
	    	min.rank = 0;
	    	min=null;

	    	consolidate(x);
	    }

	   /**
	    * public HeapNode findMin()
	    *
	    * Returns the node of the heap whose key is minimal, or null if the heap is empty.
	    * Complexity: O(1)
	    */
	    public HeapNode findMin(){
	    	if (this.isEmpty()) {return null;}{return this.min;}
	    } 
	    
	   /**
	    * public void meld (FibonacciHeap heap2)
	    *
	    * Melds heap2 with the current heap.
	    * Complexity: O(1)
	    */
	    public void meld (FibonacciHeap heap2)
	    {
	    	if (heap2.size()!=0) {  
	    		HeapNode heap2_min = heap2.findMin();
	    		last.next=heap2.first;
	    		heap2.first.prev=last;
	    		last=heap2.last;
	    		last.next=first;
	    		first.prev=last;
	    		this.trees_amount += heap2.getTressAmount();
	    		this.marked_amount += heap2.getMarkedAmount();
	    		this.size += heap2.size();
	    		if (heap2_min.getKey()<this.min.getKey()) {this.min=heap2_min;}
	    	}
	    }

	   /**
	    * public int size()
	    *
	    * Returns the number of elements in the heap.
	    * Complexity: O(1)
	    */
	    public int size(){
	    	return this.size;
	    }
	    	
	    /**
	    * public int[] countersRep()
	    *
	    * Return an array of counters. The i-th entry contains the number of trees of order i in the heap.
	    * Note: The size of of the array depends on the maximum order of a tree, and an empty heap returns an empty array.
	    * Complexity: O(n)
	    */
	    public int[] countersRep(){
	    	if (this.isEmpty()) {
	    		int[] arr = new int[]{};
	    		return arr;
	    	}	
	    	int max_rank=-1;
	    	HeapNode t = this.first;
	    	int cnt=1;
	    	while (cnt<=this.trees_amount) {
	    		int temp = t.getRank();
	    		if (temp>max_rank) {max_rank=temp;}
	    		t=t.next;
	    		cnt++;
	    	}
	    	int[] arr = new int[max_rank+1];
	    	cnt=1;
	    	HeapNode t1 = this.first;
	    	while (cnt<=trees_amount) {
	    		arr[t1.rank]++;
	    		t1=t1.next;
	    		cnt++;
	    	}
	    	return arr;
	    }
		
	   /**
	    * public void delete(HeapNode x)
	    *
	    * Deletes the node x from the heap.
		* It is assumed that x indeed belongs to the heap.
	    * Complexity: Worst case-O(n),  Amortized- O(logn)
	    */
	    public void delete(HeapNode x){    
	    	decreaseKey(x,Integer.MAX_VALUE);
	    	deleteMin();
	    }

	   /**
	    * public void decreaseKey(HeapNode x, int delta)
	    *
	    * Decreases the key of the node x by a non-negative value delta. The structure of the heap should be updated
	    * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
	    * Complexity: O(logn)
	    */
	    public void decreaseKey(HeapNode x, int delta){    
	    	x.key -= delta;
	    	if(x.key < min.key) {
				min = x;
			}
	    	if (x.parent == null) {
	    		return;
	    	}
	    	if(x.key<x.parent.key) {
	    		CascadingCut(x, x.parent);
	    	}
	    }


	   /**
	    * public int potential() 
	    *
	    * This function returns the current potential of the heap, which is:
	    * Potential = #trees + 2*#marked
	    * 
	    * In words: The potential equals to the number of trees in the heap
	    * plus twice the number of marked nodes in the heap. 
	    * Complexity: O(1)
	    */
	    public int potential() 
	    {    
	    	return this.trees_amount+2*this.marked_amount;
	    }

	   /**
	    * public static int totalLinks() 
	    *
	    * This static function returns the total number of link operations made during the
	    * run-time of the program. A link operation is the operation which gets as input two
	    * trees of the same rank, and generates a tree of rank bigger by one, by hanging the
	    * tree which has larger value in its root under the other tree.
	    * Complexity: O(1)
	    */
	    public static int totalLinks()
	    {    
	    	return links;
	    }

	   /**
	    * public static int totalCuts() 
	    *
	    * This static function returns the total number of cut operations made during the
	    * run-time of the program. A cut operation is the operation which disconnects a subtree
	    * from its parent (during decreaseKey/delete methods). 
	    * Complexity: O(1)
	    */
	    public static int totalCuts()
	    {    
	    	return cuts; 
	    }

	     /**
	    * public static int[] kMin(FibonacciHeap H, int k) 
	    *
	    * This static function returns the k smallest elements in a Fibonacci heap that contains a single tree.
	    * The function should run in O(k*deg(H)). (deg(H) is the degree of the only tree in H.)
	    *  
	    * ###CRITICAL### : you are NOT allowed to change H.
	    * Complexity: O(K*deg(H)) 
	    */
	    public static int[] kMin(FibonacciHeap H, int k)
	    {   
	    	int[] arr = new int[k];
	    	FibonacciHeap help = new FibonacciHeap();
	    	HeapNode root= H.getFirst(); 	
	    	help.insert(root.getKey(),root);
	    	int cnt=0;
	    	while(cnt<k) {
	    		HeapNode min = help.findMin();
	    		arr[cnt]=min.getKey();
	    		root=min.getSource();
	    		help.deleteMin();
	    		cnt++;
	    		if (root.getChild()!=null) {
	    			root=root.getChild();
	    			int root_key=root.getKey();
	    			help.insert(root_key,root);
	    			root=root.getNext();
	    				while (root.getKey()!=root_key) {
	    					help.insert(root.getKey(),root);
	    					root=root.getNext();
	    				}
	    		}	
	    	}
	    	
	        return arr; 
	    }
	        
	    /***********************************Additinal Functions***************************/
	    
	    public HeapNode getFirst()
	    {
	    	return this.first;
	    }
	    public int getMarkedAmount()
	    {
	    	return this.marked_amount;
	    }
	    
	    private void setMarkedAmount(int k) {
			this.marked_amount=k;
		}
	    
	    public int getTressAmount()
	    {
	    	return this.trees_amount;
	    }
	    
	    /**
	     * Construct non lazy binomial heap containing at most logn trees,
	     * at most one of each degree
	     * Complexity: W.C.-O(n), Amortized- O(logn)
	     */
	    private void consolidate(HeapNode x) {
	    	if (size == 0) {
	    		return;
	    	}
	    	to_buckets(x);
	    }
	    
	    /**
	     * First stage of consolidating.
	     * @param x
	     * Complexity: Worst case-O(n),  Amortized- O(logn)
	     */
	    private void to_buckets(HeapNode x) {
	    	double numerator = Math.log10(size);
	    	double denominator = Math.log10(1.6);
	    	int t = (int)Math.ceil(numerator/denominator); // > log_(golden rate)n. ceiling of log_1.6(n)
	    	HeapNode [] B = new HeapNode[t+10];
	    	x.prev.next = null;
	    	HeapNode y;
	    	while (x != null) {
	    		 y = x;
	    		 x = x.next;
	    		 while(B[y.rank] != null) {
	    			 y = link(y,B[y.rank]);
	    			 B[y.rank -1] = null;
	    		 }
	    		 B[y.rank] = y;
	    	}
	    	from_buckets(B);
	    }

	    /**
	     * Construct heap from trees in array B of size O(logn)
	     * @param B
	     * Complexity: O(logn)
	     */
	    private void from_buckets(HeapNode [] B) {
	    	min = null;
	    	trees_amount = 0;
	    	first = null;
	    	last = null;
	    	for (int i=0; i<B.length; i++) {
	    		if (B[i] != null) {
	    			trees_amount++;
	    			if (first==null) {
	    				first = B[i];
	    				last = B[i];
	    				min = B[i];
	    				B[i].next = B[i];
	    				B[i].prev = B[i];
	    			}
	    			else {
	    				B[i].prev = last;
	    				B[i].next = first;
	    				last.next = B[i];
	    				last = B[i];
	    				first.prev=last;
	    				if(B[i].key < min.key) {min = B[i];};
	    			}
	    		}
	    	}	
	    }

	    /**
	     * link 2 trees of the same rank k-1, to new tree of rank k.
	     * The root of the new tree is min(x.key, y.key)
	     * @param x
	     * @param y
	     * Complexity: O(1)
	     */
	    private HeapNode link(HeapNode x, HeapNode y) {
	    	links++;
	    	HeapNode new_parent;
	    	HeapNode new_child;
	    	if(x.key > y.key) {
	    		new_parent = y;
	    		new_child = x;
	    	}
	    	else {
	    		new_parent = x;
	    		new_child = y;
	    	}

	    	if(new_parent.child == null) {
	    		new_parent.child = new_child;
	    		new_child.next = new_child;
	    		new_child.prev = new_child;
	    	}else {
	    		new_child.next = new_parent.child;
	    		new_child.prev = new_parent.child.prev;
	    		new_parent.child.prev.next = new_child;
	    		new_parent.child.prev = new_child;
	    		new_parent.child = new_child;
	    	}
	    	new_child.parent = new_parent;
	    	new_parent.rank++;
	    	return new_parent;
	    }

	    /**
	     * cut x from its parent y
	     * x becomes unmarked. If y is unmarked, it becomes marked.
	     * If y is marked, CascadingCut(y,y.parent).
	     * @param x
	     * @param y
	     * Complexity: O(logn)
	     */
	    private void CascadingCut(HeapNode x,HeapNode y) {
	    	cut(x,y);
	    	if(y.parent != null) {
	    		if(y.mark == false){
	    			y.mark = true;
	    			marked_amount++;
	    			}
	    		else {
	    			CascadingCut(y,y.parent);
	    		}
	    	}
	    }
	    /**
	     * cut x from its parent y
	     * @param HeapNode x
	     * @param HeapNode y
	     * Complexity: O(1)
	     */
	    private void cut(HeapNode x,HeapNode y) {
	    	cuts++;
	    	trees_amount++;
	    	x.parent = null;
	    	if(x.mark) {
	    		x.mark = false;
	    		marked_amount--;
	    	}
	    	y.rank--;
	    	if(x.next == x) { //x is lonely child
	    		y.child = null;
	    	}else {
	    		if(y.child == x) {
	    			y.child = x.next;
	    		}
	    		x.next.prev =x.prev;
	    		x.prev.next = x.next;
	    		
	    	}
	    	x.next=first;
	    	x.prev= last;
	    	first.prev = x;
	    	last.next = x ;
	    	first = x;
	    }
	    
	    
	    /**
	     * Same as public HeapNode insert(int key), but intended for KMIN only
	     * @param key
	     * @param source
	     * Complexity: O(1)
	     */
	    private HeapNode insert(int key, HeapNode source){   
	    	HeapNode node = new HeapNode(key,source);
	    	if (this.size==0) {
	    		this.min=node;
	    		this.first=node;
	    		this.last=node;
	    	}
	    	else {
	    		if (node.key<min.key) {min=node;}
	    		HeapNode old_first = first;
	    		old_first.prev=node;
	    		first=node;
	    		first.next=old_first;
	    		first.prev=last;
	    		last.next=first;
	    		
	    	}
	    	this.size++;
	    	this.trees_amount++;
	    	return node; 
	    }


	    /******************************************************************************************/

	    
	   /**
	    * public class HeapNode
	    * 
	    * If you wish to implement classes other than FibonacciHeap
	    * (for example HeapNode), do it in this file, not in another file. 
	    *  
	    */
	    	    	
	    public static class HeapNode{

	    	public int key;
	    	private NodeInfo info;
	    	private int rank;
	    	private boolean mark;
	    	private HeapNode next;
	    	private HeapNode child;
	    	private HeapNode prev;
	    	private HeapNode parent;
	    	
	    	/**
	    	 * HeapNode constructor
	    	 * @param key
	    	 * Complexity: O(1)
	    	 */
	    	public HeapNode(int key) {
	    		this.key = key;
	    		this.info = new NodeInfo();
	    		this.rank=0;
	    		this.mark = false;
	    		this.next=null;
	    		this.prev=null;
	    		this.child=null;
	    		this.parent=null;
	    	}
	    	/**
	    	 * HeapNode constructor
	    	 * @param key
	    	 * @param source
	    	 * Complexity: O(1)
	    	 */
	    	public HeapNode(int key, HeapNode source) {//INTENDED FOR KMIN ONLY
	    		this.key = key;
	    		this.info = new NodeInfo(source);
	    		this.rank=0;
	    		this.mark = false;
	    		this.next=null;
	    		this.prev=null;
	    		this.child=null;
	    		this.parent=null;
	    	}
	    	/**
		  	 * Complexity: O(1)
		  	 */
	    	public int getKey() {
	    		return this.key;
	    	}
	    	/**
		  	 * Complexity: O(1)
		  	 */
	    	private void setKey(int key) {
	    		this.key = key;
	    	}
	    	/**
		  	 * Complexity: O(1)
		  	 */
	    	public String getInfo() {
	    		return this.info.val;
	    	}
	    	/**
		  	 * Complexity: O(1)
		  	 */
	    	public void setInfo(String s) {
	    		this.info.val = s;
	    	}
	    	/**
		  	 * Complexity: O(1)
		  	 */
	    	public HeapNode getSource() {
	    		return this.info.source;
	    	}
	    	/**
		  	 * Complexity: O(1)
		  	 */
	    	private void setSource(HeapNode source) {
	    		this.info.source = source;
	    	}  	
	    	/**
		  	 * Complexity: O(1)
		  	 */
	    	public int getRank() {
	    		return this.rank;
	    	}
	    	/**
		  	 * Complexity: O(1)
		  	 */
	    	private void setRank(Boolean bool) {
	    		if (bool) {rank++;}
	    		else {rank--;}
	    	}
	    	/**
		  	 * Complexity: O(1)
		  	 */
	    	public HeapNode getNext() { 
	    		return this.next;
	    	}
	    	/**
		  	 * Complexity: O(1)
		  	 */
	    	private void setNext(HeapNode next) {
	    		this.next=next;
	    	}
	    	/**
		  	 * Complexity: O(1)
		  	 */
	    	public HeapNode getprev() { 
	    		return this.prev;
	    	}
	    	/**
		  	 * Complexity: O(1)
		  	 */
	    	private void setPrev(HeapNode prev) {
	    		this.prev=prev;
	    	}
	    	/**
		  	 * Complexity: O(1)
		  	 */
	    	public HeapNode getChild() { 
	    		return this.child;
	    	}
	    	/**
		  	 * Complexity: O(1)
		  	 */
	    	private void setChild(HeapNode child) {
	    		this.child=child;
	    	}
	    	/**
		  	 * Complexity: O(1)
		  	 */
	    	public HeapNode getParent() { 
	    		return this.parent;
	    	}
	    	/**
		  	 * Complexity: O(1)
		  	 */
	    	private void setParent(HeapNode parent) {
	    		this.parent=parent;
	    	}
	    	/**
		  	 * Complexity: O(1)
		  	 */
	    	public boolean getMark() {
	    		return this.mark;
	    	}
	    	/**
		  	 * Complexity: O(1)
		  	 */
	    	private void setMark(Boolean bool) {
	    		this.mark=bool;
	    	}
	    	
	    	
	    	private class NodeInfo{
	    		private String val;
	    		private HeapNode source;
	    		
	    		/**
			  	 * NodeInfo constructor
			  	 * Complexity: O(1)
			  	 */
	    		public NodeInfo() {
	    			this.val=null;
	    			this.source=null;
	    		}
	    		/**
			  	 * NodeInfo constructor
			  	 * @param source
			  	 * Complexity: O(1)
			  	 */
	    		public NodeInfo(HeapNode source) {
	    			this.val=null;
	    			this.source=source;
	    		}
	    	}

	    }
	   
	}


