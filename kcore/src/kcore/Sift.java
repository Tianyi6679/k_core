package kcore;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Collections;
import java.util.HashSet;

public class Sift extends graph{
	public ArrayList<edge> edgelist1 = new ArrayList<edge>();
	public HashSet<node> visited = new HashSet<node>();
	public Sift(int _k, int[] _core, Hashtable<Integer, Integer> _fid, List<node> _nodelist) {
		super(_k, _core, _fid, _nodelist);
		visited.clear();
        for (node i:nodelist){
        	i.fake_NoN=i.NoN;
        	i.fake_neighbours.addAll(0,i.neighbours);
        	for (node j: i.neighbours){
        		if (!visited.contains(j)) edgelist1.add(new edge(i,j));
        	}
        	visited.add(i);
        }
        deleted = new HashSet<node>();
		// TODO Auto-generated constructor stub
	}
	
	public void Calculate_Contribution(int budget){
		int previous=0;
		System.out.println("Contribution calculation starts");
		long starttime = System.currentTimeMillis();
		for (int i =1; i <=edgelist1.size()/(budget); i++){
			for (int j = 0; j < budget; j++){
				previous = deleted.size();
				edge e = edgelist1.get(j);
				if (!deleted.contains(e.x) && !deleted.contains(e.y)){
					e.x.fake_neighbours.remove(e.y);
					e.y.fake_neighbours.remove(e.x);
					e.x.fake_NoN--;
					e.y.fake_NoN--;
					if (e.x.fake_NoN< k) {
						deleted.add(e.x);
						Delete(e.x);
					}
					if (e.y.fake_NoN< k) {
						deleted.add(e.y);
						Delete(e.y);
					}
					e.contribution+=(deleted.size()-previous);
				}
			}
			deleted.clear();
			for (node n:nodelist){
				n.fake_neighbours.clear();
				n.fake_neighbours.addAll(0,n.neighbours);
				n.fake_NoN=n.NoN;
			}
			Collections.shuffle(edgelist1);
			if ((i+1)%100==0) System.out.println("100 permutations");
		}
		System.out.println("Time: " + (System.currentTimeMillis()-starttime)/1000);
	}
	
	public void Delete(node n){
		List<node> cand=new ArrayList<node>();
		for (node i:n.fake_neighbours){
			if (i.fake_neighbours.remove(n))
				i.fake_NoN--;
			if (i.fake_NoN < k && !deleted.contains(i)){
				deleted.add(i);
				cand.add(i);
			}
		}
		for (node i: cand) Delete(i);
	}
	private int next(int p){
		while (deleted.contains(edgelist1.get(p).x) || deleted.contains(edgelist1.get(p).y)) p--;
		return p;
	}
	public void Pick(int budget){
		node cand1;
		node cand2;
		Calculate_Contribution(budget);
		edgelist1.sort(edge.sort_by_contri());
		deleted.clear();
		int p = edgelist1.size();
		for (int b=1; b<=budget; b++){
			p = next(p-1);
			System.out.println(edgelist1.get(p).contribution);
			cand1=edgelist1.get(p).x;
			cand2=edgelist1.get(p).y;
			cand1.neighbours.remove(cand2);
        	cand2.neighbours.remove(cand1);
        	cand1.edgelist.remove(cand2);
        	cand2.edgelist.remove(cand1);
    		cand1.NoN--;
    		cand2.NoN--;
    		if (cand1.NoN < k){
	    		deleted.add(cand1);
	    		remove_node(cand1);
    		}
    		if (cand2.NoN < k){
	    		deleted.add(cand2);
	    		remove_node(cand2);
    		}
    		if ((b)%5==0){
    			System.out.println(" Result: "+deleted.size());
    		}
			
		}
	}
	
}
