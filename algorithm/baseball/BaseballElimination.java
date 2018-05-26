import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FordFulkerson;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class BaseballElimination {

    private final int[] wins;
    private final int[] losses;
    private final int[] remains;
    private final int[][] games; // games[i][j] is the remaining number of games between team i and team j
    private final int n; // number of teams
    private final Map<String, Integer> teamToId;
    private final boolean[] eliminated;
    private final Map<String, List<String>> eliminatingTeams;

    // create a baseball division from given filename
    public BaseballElimination(String filename) {
        
        In input = new In(filename);
        n = input.readInt();
        
        // initialize instance variables
        wins = new int[n];
        losses = new int[n];
        remains = new int[n];
        games = new int[n][n];
        teamToId = new HashMap<>();
        eliminated = new boolean[n];
        eliminatingTeams = new HashMap<>();
        Map<Integer, String> idToTeam = new HashMap<>();
        
        // load game information
        for (int i = 0; i < n; i++) {
            String team = input.readString();
            teamToId.put(team, i);
            idToTeam.put(i, team);
            wins[i] = input.readInt();
            losses[i] = input.readInt();
            remains[i] = input.readInt();
            for (int j = 0; j < n; j++)
                games[i][j] = input.readInt();
        }

        // get the team id of current max win, useful to check trivial case
        int maxId = 0;
        for (int i = 1; i < n; i++)
            if (wins[maxId] < wins[i])
                maxId = i;

        // run maxflow for every team
        for (int i = 0; i < n; i++) {

            // trivial case
            if (wins[i]+remains[i] < wins[maxId]) {
                eliminated[i] = true;
                List<String> teams = new ArrayList<>();
                teams.add(idToTeam.get(maxId));
                eliminatingTeams.put(idToTeam.get(i), teams);
                continue;
            }
            
            // non-trivial case
            FlowNetwork network = constructNetwork(i);
            int s = n; // source vertex (see constructNetwork() for convention)
            int t = n+1; // target vertex
            FordFulkerson maxflow = new FordFulkerson(network, s, t);
            
            double capacity = 0;
            for (FlowEdge e: network.adj(s))
                capacity += e.capacity();
            if (maxflow.value() < capacity) // check if reached max capacity
                eliminated[i] = true;
            
            if (eliminated[i]) {
                List<String> teams =  new ArrayList<>(); // subset of eliminating teams
                for (int j = 0; j < n; j++)
                    if (maxflow.inCut(j))
                        teams.add(idToTeam.get(j));
                eliminatingTeams.put(idToTeam.get(i), teams);
            }
        }
    }

    // number of teams
    public int numberOfTeams() {
        return n;
    }

    // all teams                        
    public Iterable<String> teams() {
        return teamToId.keySet();
    }

    // number of wins for given team                               
    public int wins(String team) {
        if (team == null)
            throw new IllegalArgumentException("Input team name cannot be null!");
        if (!teamToId.containsKey(team))
            throw new IllegalArgumentException("Team " + team + " does not exist!");
        int id = teamToId.get(team);
        return wins[id];
    }             

    // number of losses for given team      
    public int losses(String team) {
        if (team == null)
            throw new IllegalArgumentException("Input team name cannot be null!");
        if (!teamToId.containsKey(team))
            throw new IllegalArgumentException("Team " + team + " does not exist!");
        int id = teamToId.get(team);
        return losses[id];      
    }

    // number of remaining games for given team                   
    public int remaining(String team) {
        if (team == null)
            throw new IllegalArgumentException("Input team name cannot be null!");
        if (!teamToId.containsKey(team))
            throw new IllegalArgumentException("Team " + team + " does not exist!");
        int id = teamToId.get(team);
        return remains[id];     
    }   

    // number of remaining games between team1 and team2            
    public int against(String team1, String team2) {
        if (team1 == null || team2 == null)
            throw new IllegalArgumentException("Input team names cannot be null!");
        if (!teamToId.containsKey(team1))
            throw new IllegalArgumentException("Team " + team1 + " does not exist!");
        if (!teamToId.containsKey(team2))
            throw new IllegalArgumentException("Team " + team2 + " does not exist!");
        int id1 = teamToId.get(team1);
        int id2 = teamToId.get(team2);
        return games[id1][id2];
    }    

    // is given team eliminated?
    public boolean isEliminated(String team) {
        if (team == null)
            throw new IllegalArgumentException("Input team name cannot be null!");
        if (!teamToId.containsKey(team))
            throw new IllegalArgumentException("Team " + team + " does not exist!");     
        int id = teamToId.get(team);
        return eliminated[id];      
    }         

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        if (team == null)
            throw new IllegalArgumentException("Input team name cannot be null!");
        if (!teamToId.containsKey(team))
            throw new IllegalArgumentException("Team " + team + " does not exist!");
        int id = teamToId.get(team);
        if (!eliminated[id])
            return null;
        return eliminatingTeams.get(team);
    }

    /*
    construct a flow network for maxflow
    vertices convention is as follows:
        - vertices 0 to n-1 are assigned as team id from 0 to n-1
        - vertice n is source
        - vertice n+1 is target
        - the last n*(n-1)/2 vertices are upper-triangle elements of game[i][j]
    in total there are n + 2 + n*(n-1)/2 vertices
    */ 
    private FlowNetwork constructNetwork(int id) {

        FlowNetwork network = new FlowNetwork(n + 2 + n*(n-1)/2);

        // connect source vertex to game vertices
        for (int i = 0; i < n; i++)
            for (int j = i+1; j < n; j++)
                if (i != id && j != id)
                    network.addEdge(new FlowEdge(n, n+2+subToInd(i, j), games[i][j]));

        // connect game vertices to team vertices
        for (int i = 0; i < n; i++)
            for (int j = i+1; j < n; j++)
                if (i != id && j != id) {
                    network.addEdge(new FlowEdge(n+2+subToInd(i, j), i, Double.POSITIVE_INFINITY));
                    network.addEdge(new FlowEdge(n+2+subToInd(i, j), j, Double.POSITIVE_INFINITY));
                }

        // connect team vertices to target vertex
        for (int i = 0; i < n; i++)
            if (i != id)
                network.addEdge(new FlowEdge(i, n+1, wins[id]+remains[id]-wins[i]));

        return network;
    }

    /*
    return the indexing of the upper triangle elements of games
    n=5 case:

            # 0 1 2 3
            # # 4 5 6
            # # # 7 8
            # # # # 9
            # # # # #
    */
    private int subToInd(int i, int j) {
        return (2*n-i-1)*i/2+j-i-1;
    }

    // client testing
    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}