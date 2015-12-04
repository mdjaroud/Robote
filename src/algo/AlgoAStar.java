package algo;

import java.util.ArrayList;

import environment.Cell;
import environment.Entrepot;
import environment.TypeCell;
import gui.EnvironmentGui;
import java.math.*;

/** this class propose an implementation of the a star algorithm */
public class AlgoAStar {

	/** nodes to be evaluated*/
	ArrayList<Cell> freeNodes;
	/** evaluated nodes*/
	ArrayList<Cell> closedNodes;

	/** Start Cell*/
	Cell start;
	/** Goal Cell*/
	Cell goal;

	/** graphe / map of the nodes*/
	Entrepot ent;
	/** gui*/
	EnvironmentGui gui;

	/**initialize the environment (100 x 100 with a density of container +- 20%) */
	AlgoAStar()
	{
		ent = new Entrepot(100, 100, 0.2, this);
		gui = new EnvironmentGui(ent);
		reCompute();
	}

	/** a* algorithm to find the best path between two states 
	 * @param _start initial state
	 * @param _goal final state*/
	ArrayList<Cell> algoASTAR(Cell _start, Cell _goal)
	{
		start = _start;
		goal = _goal;
		
		// list of visited nodes
		closedNodes = new ArrayList<Cell>();
		
		// list of nodes to evaluate
		freeNodes = new ArrayList<Cell>();
		freeNodes.add(start);
		
		// no cost to go from start to start
		// TODO: g(start) <- 0
		// TODO: h(start) <- evaluation(start)
		// TODO: f(start) <- h(start)
		start.setG((int)g(start));
		start.setH((int)h(start));
		start.setF((int)f(start));
		
		
		// while there is still a node to evaluate
		while(!freeNodes.isEmpty())
		{
			// choose the node having a F minimal
			Cell n = chooseBestNode();
			// stop if the node is the goal
			if (isGoal(n)) return rebuildPath(n);
			
			// TODO: freeNodes <- freeNodes - {n}
			// TODO: closedNodes <- closedNodes U {n}
			freeNodes.remove(n);
			closedNodes.add(n);
			
			// construct the list of neighbourgs
			ArrayList<Cell> nextDoorNeighbours  = neighboursDiag(n);
			
			//ArrayList<Cell> nextDoorNeighbours  = neighbours4(n);
			System.out.println("voisin"+" size tableau : "+nextDoorNeighbours.size());
			for(Cell ndn:nextDoorNeighbours)
			{
				//System.out.println("x : "+ndn.getX()+" y : "+ndn.getY());
				// if the neighbour has been visited, do not reevaluate it
				if (closedNodes.contains(ndn))
					continue;
				
				// cost to reach the neighbour is the cost to reach n + cost from n to the neighbourg
				//TODO: int cost = ...
				int cost = costBetween(ndn,n);
				
				boolean bestCost = false;
				// if the neighbour has not been evaluated
				if (!freeNodes.contains(ndn))
				{
					// TODO: freeNodes <- freeNodes U {ndn}
					// TODO: h(ndn) -> evaluation(ndn)
					freeNodes.add(ndn);
					ndn.setH(evaluation(ndn));
					
					bestCost = true;
				}
				else
					// if the neighbour has been evaluated to a more important cost, change its evaluation
					if (cost < ndn.getG())
						bestCost = true;
				if(bestCost)
				{
					ndn.setParent(n);
					//TODO : g(ndn) <- cost
					//TODO : f(ndn) <- g(ndn) + h(ndn)
					ndn.setG(cost);
					ndn.setF((int)f(ndn));
				}
			}
		}
		return null;
	}

	/** return the path from start to the node n*/
	ArrayList<Cell> rebuildPath(Cell n)
	{
		if (n.getParent()!=null)
		{
			ArrayList<Cell> p = rebuildPath(n.getParent());
			n.setVisited(true);
			p.add(n);
			return p;
		}
		else
			return (new ArrayList<Cell>());
	}

	/** algo called to (re)launch a star algo*/
	public void reCompute()
	{
		ArrayList<Cell>  solution = algoASTAR(ent.getStart(), ent.getGoal());
		ent.setSolution(solution);
		if (solution==null) 
			System.out.println("solution IMPOSSIBLE");
		
		gui.repaint();
	}
	

	/** return the estimation of the distance from c to the goal*/
	int evaluation(Cell c)
	{
		int estimation = (int)h(c);
		if(c.getType() == TypeCell.TERRE)
		{
			return estimation*10;
		}
		else
		{
			return estimation;
		}
		
		// TODO: cf cours : sur Terre : 10* distance vol d'oiseau entre but(goal) et c
	}

	/** return the free node having the minimal f*/
	Cell chooseBestNode()
	{
		//return goal;
		//TODO...
	
		int miniF = 9999;
		int topo = 0;
		int index = 0;
		for(int i = 0;i<freeNodes.size();i++)
		{
			topo = freeNodes.get(i).getF();
			if(topo < miniF)
			{
				index = i;
				miniF = topo;
			}
		}
		
		//System.out.println(" valeur de sortie "+ miniF);
		
		return freeNodes.get(index);
	}

	/** return weither n is a goal or not */
	boolean isGoal(Cell n)
	{
		return (n.getX() == goal.getX() && n.getY() == goal.getY());
	}

	/** return the neighbouring of a node n; a diagonal avoid the containers */
	ArrayList<Cell> neighbours(Cell n)
	{
		return closedNodes;
		// TODO: (en reponse au 3e cas)
	}

	/** return the neighbouring of a node n*/
	ArrayList<Cell> neighboursDiag(Cell n)
	{
		//On déclare la chaine qui va contenir les voisins
		ArrayList<Cell> voisins = new ArrayList<Cell>();
		
		//On récupère le terrain
		Cell[][] cellules;
		
		//Cellule temporaire qui va servir de comparaison avec la cellule en argument
		Cell cellule;
		cellules = ent.getContent();
		
		//On traverse tout le terrain à la recherche des cellules voisines de la cellule en argument
		for( int i=0 ;i<ent.getWidth() ; i++){
			for(int j=0 ;j<ent.getHeight() ; j++){
				
				//Cellule temporaire
				cellule = ent.getCell(i, j);
				
				//Comparaison avec la cellule en haut à gauche de la cellule argument
				/*
				 ___________________________________
				 |x-1, y-1 	| y-1		| x+1, y-1	|
				 ------------------------------------
				 |x-1	 	| n			| x+1		|
				 ------------------------------------
				 |x-1, y+1 	| y+1		| x+1, y+1	|
				 ------------------------------------*/
				if(cellule.getX() == n.getX()-1 && cellule.getY()== n.getY()-1){
					if(!cellule.isContainer())
						voisins.add(cellule);
				}
				else if(cellule.getY()== n.getY()-1 && cellule.getX()==n.getX()){
					if(!cellule.isContainer())
						voisins.add(cellule);
				}
				else if(cellule.getX() == n.getX()+1 && cellule.getY()== n.getY()-1){
					if(!cellule.isContainer())
						voisins.add(cellule);
				}
				else if(cellule.getX() == n.getX()-1 && cellule.getY()==n.getY()){
					if(!cellule.isContainer())
						voisins.add(cellule);
				}
				else if(cellule.getX() == n.getX()+1 && cellule.getY()==n.getY()){
					if(!cellule.isContainer())
						voisins.add(cellule);
				}
				else if(cellule.getX() == n.getX()-1 && cellule.getY()== n.getY()+1){
					if(!cellule.isContainer())
						voisins.add(cellule);
				}
				else if(cellule.getY()== n.getY()+1 && cellule.getX()==n.getX()){
					if(!cellule.isContainer())
						voisins.add(cellule);
				}
				else if(cellule.getX() == n.getX()+1 && cellule.getY()== n.getY()+1){
					if(!cellule.isContainer())
						voisins.add(cellule);
				}
			}
		}
		
		return voisins;
		// TODO: (en reponse au 1er cas)
	}

	/** return the neighbouring of a node n without permission to go in diagonal*/
	ArrayList<Cell> neighbours4(Cell n)
	{//On déclare la chaine qui va contenir les voisins
		ArrayList<Cell> voisins = new ArrayList<Cell>();
		
		//On récupère le terrain
		Cell[][] cellules;
		
		//Cellule temporaire qui va servir de comparaison avec la cellule en argument
		Cell cellule;
		cellules = ent.getContent();
		
		
		//On traverse tout le terrain à la recherche des cellules voisines de la cellule en argument
		for( int i=0 ;i<ent.getWidth() ; i++){
			for(int j=0 ;j<ent.getHeight() ; j++){
				
				//Cellule temporaire
				cellule = ent.getCell(i, j);
				
				//Comparaison avec la cellule en haut à gauche de la cellule argument
				/*
				 ___________________________________
				 |			| y-1		| 			|
				 ------------------------------------
				 |x-1	 	| n			| x+1		|
				 ------------------------------------
				 |__________|_y+1_______|___________|
				*/
				if(cellule.getX() == n.getX()-1 && cellule.getY()==n.getY()){
					if(!cellule.isContainer()){
						voisins.add(cellule);
					}
						
				}
				else if(cellule.getY()== n.getY()-1 && cellule.getX() == n.getX()){
					if(!cellule.isContainer()){
						voisins.add(cellule);
					}
				}
				else if(cellule.getX() == n.getX()+1 && cellule.getY()==n.getY()){
					if(!cellule.isContainer()){
						voisins.add(cellule);
					}
				}
				else if(cellule.getY()== n.getY()+1 && cellule.getX() == n.getX()){
					if(!cellule.isContainer()){
						voisins.add(cellule);
					}
				}
			}
		}
		
		return voisins;
		// TODO: (en reponse au 2e cas)

	}

	/** return the cost from n to c : 10 for a longitudinal move, 14 (squareroot(2)*10) for a diagonal move */
	int costBetween(Cell n, Cell c)
	{
		//longitude = Y
		// cost to reach the neighbour is the cost to reach n + cost from n to the neighbourg
		
		//TODO : sur terre, deplacement horizontal ou vertical = 10; en diagonale = 14
		
		
		int coutc = c.getG();
		int coutn = (int)g(n);
		//System.out.println("cout pour aller a n "+coutc);
		//System.out.println("+ cout jusqu'a voisin "+coutn);
		
		return (coutc+coutn);	
	}
	
	/** evaluation of the distance from start to this cell*/
	double g(Cell n)
	{
		double distance = 0;
		double positionX = (start.getX() - n.getX())*(start.getX() - n.getX());
		double positionY = (start.getY() - n.getY())*(start.getY() - n.getY());
	
		distance = Math.sqrt((double)(positionX+positionY));
		
		return distance;
	}
	
	/** evaluation of the distance from start to goal, through this cell*/
	double f(Cell n)
	{
		double distance = 0;
		distance = n.getG() + n.getH();
		
		return distance;
	}
	
	/** evaluation of the distance from this cell to the goal */
	double h(Cell n)
	{
		double distance = 0;
		double positionX = (goal.getX() - n.getX())*(goal.getX() - n.getX());
		double positionY = (goal.getY() - n.getY())*(goal.getY() - n.getY());
	
		distance = Math.sqrt((double)(positionX+positionY));
		
		return distance;
	}
	
	
	public static void main(String []args)
	{
		new AlgoAStar();

	}
}
