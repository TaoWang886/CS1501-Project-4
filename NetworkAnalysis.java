import java.util.*;
import java.io.*;

public class NetworkAnalysis{

static Node[] adj_list;
static double copper_sp=230000000;
static double fiber_sp= 200000000;

public static class Node 
{	
	private Node next;
	private int bandwidth;
	private int length;
	private String cable_type;
	private int curr;
}

public static void main(String[] args) throws Exception
{
	Scanner in = new Scanner( new File( args[0] ) );
	int v=in.nextInt();
	adj_list=new Node[v];
	
	int start;
	int end;
	int bandwidth;
	String cable_type;
	int length;
	while(in.hasNext()){
		start=in.nextInt();
		end=in.nextInt();
		cable_type=in.next();
		bandwidth=in.nextInt();
		length=in.nextInt();
		add_edge(start,end,cable_type,bandwidth,length);
	}
	do{
	Scanner user = new Scanner(System.in);
	System.out.println("---------------------------------------------------");
	System.out.println("You have three options for graph analysis:");
	System.out.println("Option1: Find the lowest latency path between any two points");
	System.out.println("Option2: Determine whether or not the graph is copper-only connected");
	System.out.println("Option3: Determine whether the graph would remain connected even if any two vertices fail. ");
	System.out.println("Please enter your choice in number (1 or 2 or 3): ");
	char option = user.nextLine().charAt(0);  // Read user input
	if(option=='1'+0){
		System.out.println("Please enter the first vertex between (0 to " + (v-1)+"): ");
		int vertex1=Integer.parseInt(user.nextLine());
		System.out.println("Please enter the second vertex: ");
		int vertex2=Integer.parseInt(user.nextLine());
		Dijkstra(vertex1,vertex2,v);
	}
	else if(option=='2'+0){
		copper_only(v);
	}
	else if(option=='3'+0){
		connected(v);
	}
	else {
		System.out.println("Illegal input! Please enter number 1 or 2 or 3!");
	}
	System.out.println("Do you want to continue? [Y/N]");
	char Y_N=user.nextLine().charAt(0);
	if (Y_N=='N'||Y_N=='n'){
		System.out.println("Bye!");
		break;}
	}while(true);

}

private static void add_edge(int start, int end, String cable_type,int bandwidth,int length)
{
	if (adj_list[start]==null) {

		adj_list[start]=new Node();
		adj_list[start].curr=end;
		adj_list[start].bandwidth=bandwidth;
		adj_list[start].length=length;
		adj_list[start].cable_type=cable_type;

	}
	else{
		Node curr=adj_list[start];
		while (curr.next!=null){
			curr=curr.next;
		}
		curr.next=new Node();
		curr.next.curr=end;
		curr.next.bandwidth=bandwidth;
		curr.next.length=length;
		curr.next.cable_type=cable_type;
	}
	if(adj_list[end]==null){
		adj_list[end]=new Node();
		adj_list[end].curr=start;
		adj_list[end].bandwidth=bandwidth;
		adj_list[end].length=length;
		adj_list[end].cable_type=cable_type;
	}
	else{
		Node curr=adj_list[end];
		while (curr.next!=null){
			curr=curr.next;
		}
		curr.next=new Node();
		curr.next.curr=start;
		curr.next.bandwidth=bandwidth;
		curr.next.length=length;
		curr.next.cable_type=cable_type;
	}
}
private static void Dijkstra(int vertex1, int vertex2,int v){
	double[] time=new double[v];
	int[] previous=new int[v];
	int[] visited=new int[v];//1 means visited; 0 means unvisited
	for(int i=0;i<v;i++)
	{
	time[i]=-1;
	previous[i]=-1;
	}

	time[vertex1]=0;
	visited[vertex1]=1;
	int min_index=vertex1;
	for (int i=0;i<v;i++){
	//System.out.println(min_index);
	Node curr=adj_list[min_index];
	while(curr!=null){
		if(visited[curr.curr]==0){
		if(curr.cable_type=="copper"){
			if(previous[curr.curr]==-1) {
				time[curr.curr]=(double) curr.length/copper_sp+time[min_index];
				previous[curr.curr]=min_index;}
			else if ((double) curr.length/copper_sp+time[min_index]<time[curr.curr]) { 
				time[curr.curr]=(double) curr.length/copper_sp+time[min_index];
				previous[curr.curr]=min_index;}}
		else{
			if(previous[curr.curr]==-1) {
				time[curr.curr]=(double) curr.length/fiber_sp+time[min_index];
				previous[curr.curr]=min_index;}
			else if((double) curr.length/fiber_sp+time[min_index]<time[curr.curr]){ 
				time[curr.curr]=(double) curr.length/fiber_sp+time[min_index];
				previous[curr.curr]=min_index;}}
		}
		curr=curr.next;
	}
	visited[min_index]=1;//mark as visited
	min_index=findMin(time, visited);
	
	}
	//for(int i=0;i<v;i++){System.out.println(previous[i]);}
	int sequence=previous[vertex2];
	String path=sequence+" -> "+vertex2;
	int minimum_bandwidth=0;
	Node curr=adj_list[sequence];
	while(curr!=null){
		if(curr.curr==vertex2){minimum_bandwidth=curr.bandwidth;break;}
		curr=curr.next;
	}
	
	while(sequence!=vertex1){
		curr=adj_list[sequence];
		while(curr!=null){
		if(curr.curr==previous[sequence]&&minimum_bandwidth>curr.bandwidth){minimum_bandwidth=curr.bandwidth;break;}
		curr=curr.next;
		}

		sequence=previous[sequence];
		path=sequence+" -> "+path;
	}
	
	System.out.println("The sequence of the path is: "+path);
	System.out.println("The minimum bandwidth along the path is "+minimum_bandwidth);
	System.out.println();
}
private static int findMin(double[] array,int [] visited){
	int index=0;
	for(int i=0;i<array.length;i++)
	{
		if(visited[i]==0&&array[i]!=-1){index=i; break;}
	}

	for(int i=0;i<array.length;i++)
	{
		if (array[i]!=-1&&array[i]<array[index]&&visited[i]==0){
			index=i;			
		}
	}
	return index;
}

private static void copper_only(int v){
	int [] id=new int[v];
	for (int i=0;i<v;i++){
		id[i]=i;
	}
	Node curr;
	for (int i=0;i<v;i++){
		curr=adj_list[i];
		while(curr!=null){

			if (curr.cable_type.equals("copper")){union(i,curr.curr,id);}
			curr=curr.next;
		}
	}
	boolean is_connected=true;
	for (int i=0;i<v;i++){
		if(id[0]!=id[i]){is_connected=false;}
	}
	if(is_connected){
		System.out.println("The graph IS copper-only connected!");
		System.out.println();
	}
	else{System.out.println("The graph IS NOT copper-only connected!");System.out.println();}
}
private static void union(int p, int q, int[] id){
	
	int pID=id[p], qID=id[q];
	if(pID==qID) return;
	for (int i=0;i<id.length;i++)
	{
		if (id[i]==pID) id[i]=qID;
	}
	
}
private static void connected(int v){
	int [] id=new int[v];
	
	Node curr;
	for (int j=0;j<v;j++){
	for (int k=0;k<v;k++){

	for (int i=0;i<v;i++){
		id[i]=i;
	}

	if(j!=k){
	for (int i=0;i<v;i++){
		if(i!=k&&i!=j) {
		curr=adj_list[i];
		while(curr!=null){

			if (curr.curr!=j&&curr.curr!=k){union(i,curr.curr,id);}
			curr=curr.next;
		}}
	}
	boolean is_connected=true;
	for (int i=0;i<v-1;i++){
		if(id[i+1]!=id[i]&&i!=j&&i!=k&&i+1!=j&&i+1!=k){is_connected=false;
			System.out.println("The graph is not connected if "+j+" and "+k+" fail.");
			System.out.println();return;}
	}}  }}
	System.out.println("The graph would remain connected even if any two vertices fail.");
	System.out.println();
}
}