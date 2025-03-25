//dorfalah
//yuvallihod
/**
 * 
 *
 * AVLTree
 *
 * An implementation of an AVL Tree with
 * distinct integer keys and info.
 *
 */


public class AVLTree {
	private static IAVLNode External_Node;	
	
	private IAVLNode root;
	private IAVLNode min;
	private IAVLNode max;
	
	
	/*
	* constructor for AVLTree. Initialize empty tree.
	* Complexity: O(1)
	*/
	public AVLTree() { 
		External_Node = new AVLNode();
		root = External_Node;
		max = External_Node;
		min = External_Node;
	}
	
	enum JoinCases{ // cases of comparison between our tree to t - smaller/bigger + higher/lower/equal 
		SH, SL,SE,
		BH, BL,BE;
	}
	
	enum Edges{
		L0R1, L1R0,
		L0R2, L2R0,
		L1R3, L3R1,
		L1R2, L2R1,
		L1R1,
		L2R2,
		L0R0;
	}
	
	enum NodeType{
		LEAF, LEFT_UNARY, RIGHT_UNARY, INTERNAL;
	}
	
	
	
	/**
	 * Look for key k in the subtree of x. used also to find insertion point
	 * @param x 
	 * @param k
	 * @pre x is real node
	 * @return the last node encountered
	 * Complexity: O(logn)
	 */
	private static IAVLNode TreePosition(IAVLNode x, int k){
		IAVLNode y = x;
		while(x.isRealNode()) {
			 y = x;
			if(k == x.getKey()) {
				return x;
			}else {
				if(k<x.getKey()) {x=x.getLeft();}
				else {x=x.getRight();}
			}
		}
		return y;
	}
	/**
	*@param node
	*@return the following node, compared by keys.
	*Complexity: O(logn)
	*/
	private IAVLNode Successor(IAVLNode node) {
		if(!node.isRealNode()) {
			return null;
		}
		if(node.getRight().isRealNode()) {
			return MinSub(node.getRight());
		}
		IAVLNode y = node.getParent();
		while(y != null && node == y.getRight()) {
			node = y;
			y = node.getParent();
		}
		return y;
	}
	
	private IAVLNode MinSub(IAVLNode x) {
		while (x.getLeft().isRealNode()) {
			x=x.getLeft();
		}
		return x;
	}
	
	private IAVLNode MaxSub(IAVLNode x) {
		while (x.getRight().isRealNode()) {
			x=x.getRight();
		}
		return x;
	}
	
	/**
	 * update nodes sizes after insert or delete. starting at the parent
	 * goes up to the root
	 * Complexity: O(logn)
	 * @param operation, true for Insert, false for Delete
	 * @param x
	 */
	private void UpdateSize(boolean operation ,IAVLNode x) {
		while(x != null && x.isRealNode()) {
			if(operation == true) {
				x.setSize(x.getSize()+1);
			}else {
				x.setSize(x.getSize()-1);
			}
			x = x.getParent();
		}
	}
	
	private void UpdateJoinSize(IAVLNode c,int size) {
		while(c !=null && c.isRealNode()) {
			c.setSize(c.getSize()+size);
			c = c.getParent();
		}
	}
	/*
	*rebalance tree after insertion
	*@param node to start rebalance from
        *@return number of rebalancing operations
	*Complexity: O(logn)
	*/
	private int rebalanceInsert(IAVLNode node) {
		if(node== null || !node.isRealNode()) {
			return 0;
		}
		Edges edges = node.getEdges();
		switch (edges) {
			case L0R1:
			case L1R0:
				node.setHeight(node.getHeight() +1);
				return 1 + rebalanceInsert(node.getParent());
			case L0R2:
				return RI02(node);
			case L2R0:
				return RI20(node);
			default:
				return 0;
		}
	}
	
	private void rebalanceJoin(IAVLNode node) {
		if(node== null || !(node.isRealNode()))  {
			return ;
		}
		Edges edges = node.getEdges();
		switch (edges) {
			case L0R1:
			case L1R0:
				node.setHeight(node.getHeight()+1);
				rebalanceJoin(node.getParent());
				break;
			case L0R2:
				IAVLNode left_son = node.getLeft();
				Edges left_edges = left_son.getEdges();
				switch(left_edges) {
					case L1R1:
						rotateRight(node);
						left_son.setHeight(left_son.getHeight()+1);
						rebalanceJoin(left_son.getParent());
						break;
					default:
						JRI02(node);
				}
				break;
			case L2R0:
				IAVLNode right_son = node.getRight();
				Edges right_edges = right_son.getEdges();
				switch(right_edges) {
				case L1R1:
					rotateLeft(node);
					right_son.setHeight(right_son.getHeight()+1);
					rebalanceJoin(right_son);
					break;
				default:
					JRI20(node);
				}
			default:
				return;
		}
	}
	/*
	*rebalance tree after insertion case 02 edges
	*@param node to start rebalance from
        *@return number of rebalancing operations
	*Complexity: O(1)
	*/
	private int RI02(IAVLNode node) {
		IAVLNode left_son = node.getLeft();
		switch (left_son.getEdges()) {
			case L1R2:
				rotateRight(node);
				node.setHeight(node.getHeight() -1);
				return 2; //1 rotation + 1 rank update
			case L2R1:
				IAVLNode b = left_son.getRight();
				rotateLeft(left_son);
				rotateRight(node);
				node.setHeight(node.getHeight() -1);
				left_son.setHeight(left_son.getHeight() -1);
				b.setHeight(b.getHeight() +1);
				return 5;//2 rotations + 3 rank update
			default:
				return 0;
		}
	}
	
	private void JRI02(IAVLNode node) {
		IAVLNode left_son = node.getLeft();
		switch (left_son.getEdges()) {
			case L1R2:
				rotateRight(node);
				node.setHeight(node.getHeight()-1);
				break;
			case L2R1:
				IAVLNode b = left_son.getRight();
				rotateLeft(left_son);
				rotateRight(node);
				node.setHeight(node.getHeight() -1);
				left_son.setHeight(left_son.getHeight() -1);
				b.setHeight(b.getHeight() +1);
			default:				
				return;
		}
	}
	/*
	*rebalance tree after insertion case 20 edges
	*@param node to start rebalance from
        *@return number of rebalancing operations
	*Complexity: O(1)
	*/
	private int RI20(IAVLNode node) {
		IAVLNode right_son = node.getRight();
		switch (right_son.getEdges()) {
			case L1R2:
				IAVLNode b = right_son.getLeft();
				rotateRight(right_son);
				rotateLeft(node);
				node.setHeight(node.getHeight() -1);
				right_son.setHeight(right_son.getHeight() -1);
				b.setHeight(b.getHeight()+1);
				return 5; //2 rotations + 3 rank update
			case L2R1:
				rotateLeft(node);
				node.setHeight(node.getHeight()-1);
				return 2;//1 rotation + 1 rank update
			default:
				return 0;
		}
	}
	
	private void JRI20(IAVLNode node) {
		IAVLNode right_son = node.getRight();
		switch (right_son.getEdges()) {
			case L1R2:
				IAVLNode b = right_son.getLeft();
				rotateRight(right_son);
				rotateLeft(node);
				node.setHeight(node.getHeight() -1);
				right_son.setHeight(right_son.getHeight() -1);
				b.setHeight(b.getHeight()+1);
				break;
			case L2R1:
				rotateLeft(node);
				node.setHeight(node.getHeight()-1);
			default:
				return;
		}
	}
	/*
	*rotate tree right
	*@param node to apply rotation 
	*Complexity: O(1)
	*/
	private void rotateRight(IAVLNode y) {
		IAVLNode x = y.getLeft();
		IAVLNode a = x.getLeft();
		IAVLNode b = x.getRight();
		IAVLNode c = y.getRight();
		
		if (y==root) {
			root = x;
		}else if (y.isLeftSon()) {
			y.getParent().setLeft(x);
		}else {
			y.getParent().setRight(x);
		}
		x.setParent(y.getParent());
		x.setRight(y);
		y.setParent(x);
		y.setLeft(b);
		b.setParent(y);
		
		y.setSize(b.getSize() + c.getSize() +1);
		x.setSize(y.getSize() + a.getSize() +1);
	}
	
	/*
	*rotate tree left
	*@param node to apply rotation 
	*Complexity: O(1)
	*/
	private void rotateLeft(IAVLNode x) {
		IAVLNode y = x.getRight();
		IAVLNode a = x.getLeft();
		IAVLNode b = y.getLeft();
		IAVLNode c =y.getRight();
		
		if(x==root) {
			root = y;
		}else if(x.isLeftSon()) {
			x.getParent().setLeft(y);
		}else {
			x.getParent().setRight(y);
		}
		y.setParent(x.getParent());
		y.setLeft(x);
		x.setParent(y);
		x.setRight(b);
		b.setParent(x);
		
		
		x.setSize(a.getSize() + b.getSize() +1);
		y.setSize(x.getSize() + c.getSize() +1);
	}
	
	/*
	*delete leaf
	*@param leaf to be deleted
	*Complexity: O(1)
	*/
	private IAVLNode deleteLeaf (IAVLNode leaf) {
		if(leaf == root) {
			root = External_Node;
			min = External_Node;
			max = External_Node;
		}else {
			if(leaf.isLeftSon()) {
				leaf.getParent().setLeft(External_Node);
			}else {
				leaf.getParent().setRight(External_Node);
			}
		}
		return leaf.getParent();
	}
	
	/*
	*delete right unary node
	*@param node to be deleted
	*Complexity: O(1)
	*/
	private IAVLNode deleteRightUnary(IAVLNode unary) {
		IAVLNode son = unary.getRight();
		son.setParent(unary.getParent());
		if (unary == root) {
			root = son;
		}else {
			if(unary.isLeftSon()) {
				unary.getParent().setLeft(son);
			}else {
				unary.getParent().setRight(son);
			}
		}
		return unary.getParent();
	}
	
	/*
	*delete left unary node
	*@param node to be deleted
	*Complexity: O(1)
	*/
	private IAVLNode deleteLeftUnary(IAVLNode unary) {
		IAVLNode son = unary.getLeft();
		son.setParent(unary.getParent());
		if (unary == root) {
			root = son;
		}else {
			if(unary.isLeftSon()) {
				unary.getParent().setLeft(son);
			}else {
				unary.getParent().setRight(son);
			}
		}
		return unary.getParent();
	}
	
	/*
	*rebalance tree after deletion
	*@param node to start rebalance from
        *@return number of rebalancing operations
	*Complexity: O(logn)
	*/
	private int rebalanceDelete(IAVLNode node) {
		if(node== null || !node.isRealNode()) {
			return 0;
		}
		Edges edges = node.getEdges();
		switch (edges) {
			case L2R2:
				node.setHeight(node.getHeight() -1);
				return 1 + rebalanceDelete(node.getParent());
			case L3R1:
				return RD31(node);
			case L1R3:
				return RD13(node);
			default:
				return 0;
		}
	}
	
	/*
	*rebalance tree after deletion case 31 edges
	*@param node to start rebalance from
        *@return number of rebalancing operations
	*Complexity: O(1)
	*/
	private int RD31(IAVLNode node) {
		IAVLNode right_son = node.getRight();
		switch (right_son.getEdges()) {
			case L1R1:
				rotateLeft(node);
				node.setHeight(node.getHeight() -1);
				right_son.setHeight(right_son.getHeight()+1);
				return 3; // 1 rotation + 2 rank updates
			case L2R1:
				rotateLeft(node);
				node.setHeight(node.getHeight() -2);
				return 2 + rebalanceDelete(right_son.getParent());//1 rotation + 1 rank update
			case L1R2:
				IAVLNode a = right_son.getLeft();
				rotateRight(right_son);
				rotateLeft(node);
				node.setHeight(node.getHeight() -2);
				right_son.setHeight(right_son.getHeight() -1);
				a.setHeight(a.getHeight()+1);
				return 5 + rebalanceDelete(a.getParent());//2 rotations + 3 rank updates
			default:
				return 0;
		}
	}
	
	/*
	*rebalance tree after deletion case 13 edges
	*@param node to start rebalance from
        *@return number of rebalancing operations
	*Complexity: O(1)
	*/
	private int RD13(IAVLNode node) {
		IAVLNode left_son = node.getLeft();
		switch (left_son.getEdges()) {
			case L1R1:
				rotateRight(node);
				node.setHeight(node.getHeight() -1);
				left_son.setHeight(left_son.getHeight() +1);
				return 3; //1 rotation + 2 rank updates
			case L1R2:
				rotateRight(node);
				node.setHeight(node.getHeight() -2);
				return 2 + rebalanceDelete(left_son.getParent());
			case L2R1:
				IAVLNode a = left_son.getRight();
				rotateLeft(left_son);
				rotateRight(node);
				node.setHeight(node.getHeight() -2);
				left_son.setHeight(left_son.getHeight()-1);
				a.setHeight(a.getHeight()+1);
				return 5 + rebalanceDelete(a.getParent());
		default:
			return 0;
		}
	}
	
	private void sortKeysRec(IAVLNode node,int[] keys_array, int counter) { 
		  if(node.isRealNode()) {
			  sortKeysRec(node.getLeft(),keys_array,counter);
			  counter= counter + node.getLeft().getSize();
			  keys_array[counter]= node.getKey();
			  counter++;
			  sortKeysRec(node.getRight(),keys_array,counter);
		  }
	  }
	
	private void sortInfoRec(IAVLNode node,String[] info_array, int counter) { 
		  if(node.isRealNode()) {
			  sortInfoRec(node.getLeft(),info_array,counter);
			  counter+=node.getLeft().getSize();
			  info_array[counter]= node.getValue();
			  sortInfoRec(node.getRight(),info_array,counter+1);
		  }
	  }
	
	private JoinCases getCases(AVLTree t, int heights_delta){
  		boolean smaller = (this.getRoot().getKey() < t.getRoot().getKey());
  		if (smaller) {
  			if(heights_delta == 0) {return JoinCases.SE;} // smaller keys than t,same height
	  		if (heights_delta > 0) {return JoinCases.SH;} // smaller keys than t,higher 
	  		if (heights_delta < 0) {return JoinCases.SL;} // smaller keys than t,lower		
  		}
  		if(heights_delta == 0) {return JoinCases.BE;} // bigger keys than t,same height
  		if (heights_delta > 0) {return JoinCases.BH;} // bigger keys than t,higher
  		return JoinCases.BL;// bigger keys than t,lower
  	}
	
	private void EJoin(IAVLNode x,AVLTree t,boolean smaller) {
		IAVLNode left;
		IAVLNode right;
		if (smaller) { // means SE
			left = this.root;
			right = t.getRoot();
			this.max= t.max;
		}
		else { // means BE
			right = root;
			left = t.getRoot();
			this.min = t.min;
		}
		root = x;
		root.setLeft(left);
		root.setRight(right);
		left.setParent(x);
		right.setParent(x);
		root.setSize(root.getLeft().getSize() + root.getRight().getSize() +1);
		root.setHeight(left.getHeight() +1);
	}
	private void LJoin(IAVLNode x,AVLTree t,boolean s) {
		IAVLNode rank_left;
		IAVLNode rank_right;
		IAVLNode right;
		IAVLNode new_root;
		if (s) { // means SL
			rank_left = this.getRoot();
			right = t.getRoot();
			new_root = t.getRoot();
			this.max=t.max;
			
		}
		else { // means BH
			right = this.getRoot();
			rank_left = t.getRoot();
			new_root = this.getRoot();
			this.min = t.min;
		}
		int curry_size = rank_left.getSize()+1; // 1 is for x size
		int rank = rank_left.getHeight();
		IAVLNode c = right;
		rank_right = GetRankL(right,rank,c );
		c.setLeft(x);
		UpdateRelations(x,c,rank_left,rank_right);
		x.setHeight(rank+1);
		x.setSize(rank_right.getSize()+curry_size); // we add the other tree size to x size
		UpdateJoinSize(c,curry_size); // increase the size of c and his ancient parents with curry_size
		this.root = new_root;
		rebalanceJoin(c);

		
	}
		
		
		private void HJoin(IAVLNode x,AVLTree t,boolean s) {
			IAVLNode rank_left;
			IAVLNode rank_right;
			IAVLNode left;
			IAVLNode new_root;
			if (s) { // means BL
				rank_right = this.getRoot();
				left = t.getRoot();
				new_root = t.getRoot();
				this.min = t.min;
			}
			else { // means SH
				left = this.getRoot();
				rank_right = t.getRoot();
				new_root = this.getRoot();
				this.max=t.max;
			}
			int curry_size = rank_right.getSize()+1; // 1 is for x size
			int rank = rank_right.getHeight();
			IAVLNode c = left;
			rank_left = GetRankR(left,rank,c);	
			c.setRight(x);
			UpdateRelations(x,c,rank_left,rank_right);
			x.setHeight(rank+1);
			x.setSize(rank_left.getSize() +curry_size); // we add the other tree size to x size
			UpdateJoinSize(c,curry_size); // increase the size of c and his ancient parents with curry_size
			this.root = new_root;
			rebalanceJoin(c);
		
		
		
	}
	private void UpdateRelations(IAVLNode x, IAVLNode c,IAVLNode rank_left,IAVLNode rank_right) {
		x.setParent(c);
		x.setLeft(rank_left);
		rank_left.setParent(x);
		x.setRight(rank_right);
		rank_right.setParent(x);
	}
	
	private IAVLNode GetRankL(IAVLNode right,int rank, IAVLNode c) { // gets to the rank while moving LEFT
		while (right.getHeight()>rank) {
			c=right;
			right= right.getLeft();
		}
		return right;
	}
	private IAVLNode GetRankR(IAVLNode left,int rank, IAVLNode c) { // gets to the rank while moving RIGHT
		while (left.getHeight()>rank) {
			c=left;
			left= left.getRight();
			
		}
		return left;
	}

	
	
   /*************************Default functions below*****************************/
  /**
   * public boolean empty()
   * Returns true if and only if the tree is empty.
   * Complexity: O(1)
   */
  public boolean empty() {
    return root.getSize()==0;
  }

 /**
   * public String search(int k)
   *
   * Returns the info of an item with key k if it exists in the tree.
   * otherwise, returns null.
   * Complexity: O(logn)
   */
  public String search(int k){
	  IAVLNode x = root;
	  while (x != null) {
		  if (k == x.getKey()) {
			  return x.getValue();
		  }else {
			  if(k<x.getKey()) {
				  x=x.getLeft();
			  }else {
				  x=x.getRight();
			  }
		  }  
	  }
	  return null;
  }

  /**
   * public int insert(int k, String i)
   *
   * Inserts an item with key k and info i to the AVL tree.
   * The tree must remain valid, i.e. keep its invariants.
   * Returns the number of re-balancing operations, or 0 if no re-balancing operations were necessary.
   * A promotion/rotation counts as one re-balance operation, double-rotation is counted as 2.
   * Returns -1 if an item with key k already exists in the tree.
   * Complexity: O(logn)
   */
   public int insert(int k, String i) {
	  if (empty()) {
		  root = min = max = new AVLNode(k, i, null);
		  return 0;
	  }
	  AVLNode y = (AVLNode)TreePosition(root , k);
	  if(y.getKey()==k) {
		  return -1;
	  }
	  AVLNode node = new AVLNode(k, i , y);
	  if(y.getKey() < k) {
		  y.setRight(node);
		  if(y.getKey()==max.getKey()){ max = node;}//update max
	  }else {
		  y.setLeft(node);
		  if(y.getKey()==min.getKey()){min = node;}//update min
	  }
	  UpdateSize(true,y);
	   
	   return rebalanceInsert(y);
   }

  /**
   * public int delete(int k)
   *
   * Deletes an item with key k from the binary tree, if it is there.
   * The tree must remain valid, i.e. keep its invariants.
   * Returns the number of re-balancing operations, or 0 if no re-balancing operations were necessary.
   * A promotion/rotation counts as one re-balance operation, double-rotation is counted as 2.
   * Returns -1 if an item with key k was not found in the tree.
   * Complexity: O(logn)
   */
   public int delete(int k){
	   IAVLNode y = TreePosition(root,k);
	   if(empty() || y.getKey()!=k){ // key is not in the tree
		   return -1;
	   }
	   if (y==min) {min = Successor((IAVLNode)y);} //update min
	   if (y==max) { //update max
		   if(y.getLeft().isRealNode()) {
			   IAVLNode node = y.getLeft();
			   while(node.getRight().isRealNode()) {
				   node=node.getRight();
			   }
			   max = node;
		   }else {
			   max = y.getParent();
		   }
	   }
	   IAVLNode start;
	   switch (y.type()) {
	   	   case INTERNAL:
	   		   IAVLNode successor = Successor(y);
	   		   if(successor.type()== NodeType.LEAF) {
	   			   start = deleteLeaf(successor);
	   		   }else { //successor.type() = NodeType.RIGHT_UNARY.GOT ONLY 2 OPTIONS
	   			   start = deleteRightUnary(successor);
	   		   }
	   		   successor.setSize(y.getSize());
	   		   successor.setHeight(y.getHeight());
	   		   successor.setParent(y.getParent());
	   		   successor.setRight(y.getRight());
	   		   successor.setLeft(y.getLeft());
	   		   if(y == root) {
	   			   root = successor;
	   		   }else {
	   			   if (y.isLeftSon()) {
	   				   y.getParent().setLeft(successor);
	   			   }else {
	   				   y.getParent().setRight(successor);
	   			   }
	   		   }
	   		   UpdateSize(false, start);
	   		   break;
	   	   case RIGHT_UNARY:
	   		   start = deleteRightUnary(y);
	   		   UpdateSize(false,start);
	   		   break;
	   	   case LEFT_UNARY:
	   		   start = deleteLeftUnary(y);
	   		   UpdateSize(false,start);
	   		break;
	   	   default: //case leaf
	   		 start = deleteLeaf(y);
	   		 UpdateSize(false,start);
	   }
	   
	   return rebalanceDelete(start);
   }

   /**
    * public String min()
    *
    * Returns the info of the item with the smallest key in the tree,
    * or null if the tree is empty.
    * Complexity: O(1)
    */
   public String min(){
	   if (empty()) {
		   return null;
	   }
	   return min.getValue();
   }

   /**
    * public String max()
    *
    * Returns the info of the item with the largest key in the tree,
    * or null if the tree is empty.
    * Complexity: O(1)
    */
   public String max(){
	   if (empty()) {
		   return null;
	   }
	   return max.getValue();
   }

  /**
   * public int[] keysToArray()
   *
   * Returns a sorted array which contains all keys in the tree,
   * or an empty array if the tree is empty.
   */
   public int[] keysToArray()
   {
         if (empty()) {
         	return new int[0];
         }
         int[] keys_array = new int[root.getSize()];
         int counter=0;
         sortKeysRec(root,keys_array,counter);
         return keys_array;
         
   }

  /**
   * public String[] infoToArray()
   *
   * Returns an array which contains all info in the tree,
   * sorted by their respective keys,
   * or an empty array if the tree is empty.
   */
   public String[] infoToArray()
   {
       if (empty()) {
       	return new String[0];
       }
       String[] info_array = new String[root.getSize()];
       int counter=0;
       sortInfoRec(root,info_array,counter);
       return info_array;
       
 }

   /**
    * public int size()
    *
    * Returns the number of nodes in the tree.
    * Complexity: O(1)
    */
   public int size(){
	   return root.getSize();
   }
   
   /**
    * public int getRoot()
    *
    * Returns the root AVL node, or null if the tree is empty
    * Complexity: O(1)
    */
   public IAVLNode getRoot(){
	   if (empty()) {
		   return null;
	   }
	   return root;
   }
   
   /**
    * public AVLTree[] split(int x)
    *
    * splits the tree into 2 trees according to the key x. 
    * Returns an array [t1, t2] with two AVL trees. keys(t1) < x < keys(t2).
    * 
    * precondition: search(x) != null (i.e. you can also assume that the tree is not empty)
    * postcondition: none
    */   
   public AVLTree[] split(int x)
   {
	   AVLTree t1 = new AVLTree();
	   AVLTree t2 = new AVLTree();
	   IAVLNode y = TreePosition(root,x);
	   if (y.getLeft()!=null) {t1.root = y.getLeft();}
	   else {t1.root = External_Node;}
	   t1.root.setParent(External_Node);
	   if (t1.root.isRealNode()) {
		   t1.min = MinSub(t1.getRoot());
		   t1.max = MaxSub(t1.getRoot());
	   }
	   if (y.getRight()!=null) {t2.root = y.getRight();}
	   else {t2.root = External_Node;}
	   t2.root.setParent(External_Node);
	   if (t2.root.isRealNode()) {
		   t2.min = MinSub(t2.getRoot());
		   t2.max = MaxSub(t2.getRoot());
	   }
	   
	   IAVLNode k =y.getParent();
	   while(k!=null&& k.isRealNode()) {
		   IAVLNode parent = k.getParent();
		   if (k.getRight()==y) {
			   AVLTree temp = new AVLTree();
			   temp.root=k.getLeft();
			   temp.root.setParent(External_Node);
			   temp.join(k, t1);
			   t1 = temp;
			   t1.root.setParent(External_Node);

		   }
		   else {
			   AVLTree temp = new AVLTree();
			   temp.root=k.getRight();
			   temp.root.setParent(External_Node);
			   temp.join(k, t2);
			   t2 = temp;
			   t2.root.setParent(External_Node);
		   }
		  y=k;
		  k=parent; 
	   }
	   AVLTree[] result = new AVLTree[] {t1,t2};
	   return result;   
   }
   
   /**
    * public int join(IAVLNode x, AVLTree t)
    *
    * joins t and x with the tree. 	
    * Returns the complexity of the operation (|tree.rank - t.rank| + 1).
	*
	* precondition: keys(t) < x < keys() or keys(t) > x > keys(). t/tree might be empty (rank = -1).
    * postcondition: none
    */   
   public int join(IAVLNode x, AVLTree t){ 
	   if (this.empty()) {
	   		if (t.empty()) {
	   			x.setLeft(External_Node);
	   			x.setRight(External_Node);
	   			x.setSize(1);
	   			root = min = max = x;
	   			return 1;
	   		}
	   		else {
	   			this.root=t.root;
	   			this.min=t.min;
	   			this.max=t.max;
	   			IAVLNode y = t.TreePosition(root , x.getKey());
	   		  if(y.getKey() < x.getKey()) {
	   			  y.setRight(x);
	   			  if(y.getKey()==max.getKey()){ max = x;}//update max
	   		  }else {
	   			  y.setLeft(x);
	   			  if(y.getKey()==min.getKey()){min = x;}//update min
	   		  }
	   		  t.UpdateJoinSize(y,1);
	   		  t.rebalanceJoin(y);
	   		  return t.getRoot().getHeight()+1;
	   		}
  		}
	   else if (t.empty()){		 		
		   AVLNode y = (AVLNode)TreePosition(root , x.getKey());
		   if(y.getKey() < x.getKey()) {
	   			  y.setRight(x);
	   			  if(y.getKey()==max.getKey()){ max = x;}//update max
	   		  }else {
	   			  y.setLeft(x);
	   			  if(y.getKey()==min.getKey()){min = x;}//update min
	   		  }
	   		  UpdateJoinSize(y,1);
	   		  rebalanceJoin(y);
	   		  return this.getRoot().getHeight()+1;
	   		}
	   int heights_delta = (this.getRoot().getHeight()-t.getRoot().getHeight());
	   JoinCases cases = getCases(t,heights_delta);
	   switch (cases) {
			case SE:
					EJoin(x,t,true);
					break;
			case BE:
					EJoin(x,t,false);
					break;
			case SL:
					LJoin(x,t,true);
					break;
			case BH:
					LJoin(x,t,false);
					break;
			case SH:
					HJoin(x,t,false);
					break;
			case BL:
					HJoin(x,t,true);
	   }
	   int cost = Math.abs(heights_delta) + 1;
	   return cost;   	   
   }

	/** 
	 * public interface IAVLNode
	 * ! Do not delete or modify this - otherwise all tests will fail !
	 */
	public interface IAVLNode{	
		public int getKey(); // Returns node's key (for virtual node return -1).
		public String getValue(); // Returns node's value [info], for virtual node returns null.
		public void setLeft(IAVLNode node); // Sets left child.
		public IAVLNode getLeft(); // Returns left child, if there is no left child returns null.
		public void setRight(IAVLNode node); // Sets right child.
		public IAVLNode getRight(); // Returns right child, if there is no right child return null.
		public void setParent(IAVLNode node); // Sets parent.
		public IAVLNode getParent(); // Returns the parent, if there is no parent return null.
		public boolean isRealNode(); // Returns True if this is a non-virtual AVL node.
    	public void setHeight(int height); // Sets the height of the node.
    	public int getHeight(); // Returns the height of the node (-1 for virtual nodes).
    	/***********************************************/
    	public int getSize();
    	public void setSize(int size);
    	public Edges getEdges();
    	public boolean isLeftSon();
    	public NodeType type();
	}

   /** 
    * public class AVLNode
    *
    * If you wish to implement classes other than AVLTree
    * (for example AVLNode), do it in this file, not in another file. 
    * 
    * This class can and MUST be modified (It must implement IAVLNode).
    */
  public class AVLNode implements IAVLNode{
	  	private  int key;
	  	private String info;
	  	
	  	private AVLNode parent;
	  	private AVLNode right;
	  	private AVLNode left;
	  	
	  	private int rank;
	  	private int size;
	  	
	  	/**
	  	 * private AVLNode constructor for external node
		 * Complexity: O(1)
	  	 */
	  	private AVLNode() {
	  		key = -1;
	  		rank = -1;
	  		size = 0;
	  	}
	  	
	  	/**
	  	 * AVLNode constructor
	  	 * @param key
	  	 * @param info
	  	 * @param parent
		 * Complexity: O(1)
	  	 */
	  	public AVLNode(int key, String info, AVLNode parent) {
	  		this.key = key;
	  		this.info = info;
	  		this.parent = parent;
	  		right = (AVLNode)External_Node;
	  		left = (AVLNode)External_Node;
	  		rank=0;
	  		size=1;
	  	}
	  	
	  	/*
		*@return the type of node edges
		*Complexity: O(1)
		*/
	  	public Edges getEdges() {
	  		int rightDiff = rank - right.rank;
	  		int leftDiff = rank - left.rank;
	  		
	  		if(leftDiff == 0) {
	  			if (rightDiff == 1) {return Edges.L0R1;}
	  			if (rightDiff == 2) {return Edges.L0R2;}
	  		}
	  		if(leftDiff == 1) {
	  			if(rightDiff == 0) {return Edges.L1R0;}
	  			if(rightDiff == 1) {return Edges.L1R1;}
	  			if(rightDiff == 2) {return Edges.L1R2;}
	  			if(rightDiff == 3) {return Edges.L1R3;}
	  		}
	  		if(leftDiff == 2) {
	  			if(rightDiff == 0) {return Edges.L2R0;}
	  			if(rightDiff == 1) {return Edges.L2R1;}
	  			if(rightDiff == 2) {return Edges.L2R2;}
	  		}
	  		if(leftDiff == 3) {
	  			if(rightDiff == 1) {return Edges.L3R1;}
	  		}
	  		return Edges.L0R0;
	  	}
	  	
		/*
		*@return true iff node is left son
		*Complexity: O(1)
		*/
	  	public boolean isLeftSon() {
	  		return (parent != null && parent.key> key);
	  	}
	  	/*
		*@return node type(internal/right unary/left unary/leaf)
		*Complexity: O(1)
		*/
	  	public NodeType type() {
	  		if(right.isRealNode()) {
	  			if(left.isRealNode()) {
	  				return NodeType.INTERNAL;
	  			}else {
	  				return NodeType.RIGHT_UNARY;
	  			}
	  		}else {
	  			if(left.isRealNode()) {
	  				return NodeType.LEFT_UNARY;
	  			}
	  			return NodeType.LEAF;
	  		}
	  	}
	  	/*
		*Complexity: O(1)
		*/
	  	public int getSize() {
	  		return size;
	  	}
	  	/*
		*Complexity: O(1)
		*/
	  	public void setSize(int size) {
	  		this.size = size;
	  	}
	  
	  
	  /****************************** interface implementation*************************/	
	  
	  	/**
	  	 * Complexity: O(1)
	  	 */
		public int getKey(){
			return key;
		}
		
		/**
	  	 * Complexity: O(1)
	  	 */
		public String getValue(){
			return info;
		}
		
		/**
	  	 * Complexity: O(1) 
	  	 */
		public void setLeft(IAVLNode node){  
			left = (AVLNode)node;
		}
		
		/**
	  	 * Complexity: O(1)
	  	 */
		public IAVLNode getLeft(){
			return left;
		}
		
		/**
	  	 * Complexity: O(1)
	  	 */
		public void setRight(IAVLNode node){ 
			right =  (AVLNode)node;
		}
		
		/**
	  	 * Complexity: O(1)
	  	 */
		public IAVLNode getRight(){
			return right;
		}
		
		/**
	  	 * Complexity: O(1)
	  	 */
		public void setParent(IAVLNode node) {
			parent =  (AVLNode)node;
		}
		
		/**
	  	 * Complexity: O(1)
	  	 */
		public IAVLNode getParent(){
			return parent;
		}
		
		/**
	  	 * Complexity: O(1)
	  	 */
		public boolean isRealNode(){
			return rank != -1; 
		}
		
		/**
	  	 * Complexity: O(1)
	  	 */
	    public void setHeight(int height){
	     rank = height;
	    }
	    
	    /**
	  	 * Complexity: O(1)
	  	 */
	    public int getHeight(){
	      return rank;
	    }
  }
}
  
