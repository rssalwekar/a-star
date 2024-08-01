import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;
import static java.lang.System.exit;

public class Main {

    //constants for console output colors
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";

    //constants for world generation
    public static final int size = 15;
    public static final int chanceOfWall = 10;


    //calculate cost of moving from current node to successor node
    public static int calcCost(Node current, Node successor) {
        //if the successor is a wall, return a high cost
        if (successor.getType() == 1) {
            return 10000;
        }
        //if the successor is a diagonal, return a high cost
        if (current.getRow() != successor.getRow() && current.getCol() != successor.getCol()) {
            return 14;
        }
        //if the successor is a straight node, return a low cost
        return 10;
    }

    //calculate heuristic using Manhattan distance
    public static int calcHeuristic(Node start, Node goal) {
        return (Math.abs(start.getRow() - goal.getRow()) + Math.abs(start.getCol() - goal.getCol())) * 10;
    }

    //generate world and print functions
    public static Node[][] generateWorld() {
        //create 2D array of nodes
        Node[][] world = new Node[size][size];
        Random random = new Random();
        int randomNumber, t;

        for (int row = 0; row < world.length; row++) {
            for (int col = 0; col < world[row].length; col++) {
                //randomly assign type to node
                randomNumber = random.nextInt(100);
                //90% chance of being traversable
                if (randomNumber < chanceOfWall) {
                    t = 1;
                } else {
                    t = 0;
                }
                //create node and add to world
                world[row][col] = new Node(row, col, t);
            }
        }

        return world;
    }

    public static void printLegend() {
        System.out.println("\nLegend:");
        System.out.println("X - Wall");
        System.out.println("_ - Open space");
        System.out.println(ANSI_GREEN + "*" + ANSI_RESET + " - Path\n");
    }

    public static void printWorld(Node[][] world) {
        System.out.print("   ");
        for (int i = 0; i < world[0].length; i++) {
            if (i < 10) {
                System.out.print(i + "  ");
            } else {
                System.out.print(i + " ");
            }
        }
        System.out.println();

        for (int row = 0; row < world.length; row++) {
            if (row < 10) {
                System.out.print(row + "  ");
            } else {
                System.out.print(row + " ");
            }

            for (int col = 0; col < world[row].length; col++) {
                if (world[row][col].getType() == 1) {
                    System.out.print("X  ");
                } else {
                    System.out.print("_  ");
                }
            }
            System.out.println();
        }
    }

    public static void printSolution(Node[][] world, ArrayList<Node> path) {
        System.out.print("   ");
        for (int i = 0; i < world[0].length; i++) {
            if (i < 10) {
                System.out.print(i + "  ");
            } else {
                System.out.print(i + " ");
            }
        }
        System.out.println();

        for (int row = 0; row < world.length; row++) {
            if (row < 10) {
                System.out.print(row + "  ");
            } else {
                System.out.print(row + " ");
            }

            for (int col = 0; col < world[row].length; col++) {
                if (path.contains(world[row][col])) {
                    System.out.print(ANSI_GREEN + "*  " + ANSI_RESET);
                } else if (world[row][col].getType() == 1) {
                    System.out.print("X  ");
                } else {
                    System.out.print("_  ");
                }
            }
            System.out.println();
        }
    }

    //generate neighbors and add to open list
    //used in main while loop to handle neighbors of current node
    public static void generateNeighbors(MinHeap open, ArrayList<Node> closed, Node currentNode, Node goal, Node[][] world) {
        //generate neighbors and add to open list
        for (int row = -1; row <= 1; row++) {
            for (int col = -1; col <= 1; col++) {
                //skip the current node
                if (row == 0 && col == 0) {
                    continue;
                }

                //get adjacent row and col
                int adjacentRow = currentNode.getRow() + row;
                int adjacentCol = currentNode.getCol() + col;

                //if adjacent node is out of bounds, skip
                if (adjacentRow < 0 || adjacentRow >= world.length || adjacentCol < 0 || adjacentCol >= world[adjacentRow].length) {
                    continue;
                }

                //get adjacent node
                Node adjacent = world[adjacentRow][adjacentCol];

                //if adjacent node is not traversable, skip
                if (adjacent.getType() == 1) {
                    continue;
                }

                //if adjacent node is in the closed list, skip
                if (closed.contains(adjacent)) {
                    continue;
                }

                //if adjacent node is in the open list, skip or update
                if (open.contains(adjacent)) {
                    int currCost = calcCost(currentNode, adjacent);
                    int fromStartCost = currentNode.getG() + currCost;
                    // if the cost to get to the adjacent node from the current node
                    // is greater than the cost to get to the adjacent node
                    // from the start node, skip
                    if (fromStartCost >= currCost) {
                        continue;
                    }
                    adjacent.setG(fromStartCost);
                    adjacent.setF();
                    adjacent.setParent(currentNode);
                    open.reheapify();
                } else {
                    //calculate cost, heuristic, and f value for adjacent node
                    adjacent.setG(currentNode.getG() + calcCost(currentNode, adjacent));
                    adjacent.setH(calcHeuristic(adjacent, goal));
                    adjacent.setF();

                    //set parent of adjacent node to current node
                    adjacent.setParent(currentNode);

                    //add adjacent node to open list
                    open.add(adjacent);
                }
            }
        }
    }

    //main while loop to find path
    //used in overall solve function
    public static void mainWhileLoop(MinHeap open, ArrayList<Node> closed, Node goal, Node[][] world) {
        //while open list is not empty
        while (!open.isEmpty()) {
            Node currentNode = open.remove();

            //if current node is the goal node, we have found the path
            if (currentNode.equals(goal)) {
                System.out.println(ANSI_GREEN + "Path found!" + ANSI_RESET);
                ArrayList<Node> path = new ArrayList<>();
                Node current = currentNode;
                while (current != null) {
                    path.add(0, current);
                    current = current.getParent();
                }
                for (Node n : path) {
                    System.out.println(n);
                }

                printSolution(world, path);
                return;
            }

            generateNeighbors(open, closed, currentNode, goal, world);

            //add current node to closed list
            closed.add(currentNode);
        }

        //if open list is empty, no path is found
        System.out.println(ANSI_RED + "No path could be found." + ANSI_RESET);
    }

    //overall solve function for simplicity and readability

    public static void solve(Node start, Node goal, Node[][] world) {
        MinHeap open = new MinHeap();
        ArrayList<Node> closed = new ArrayList<>(100);

        //calculate cost, heuristic, and f value for start node
        start.setG(0);
        start.setH(calcHeuristic(start, goal));
        start.setF();

        //add start node to open list
        open.add(start);

        mainWhileLoop(open, closed, goal, world);
    }

    //function to test if minHeap is working
    public static void testHeap() {
        MinHeap heap = new MinHeap();

        // Creating nodes with different g and h values
        Node node1 = new Node(0, 0, 0);
        node1.setG(10);
        node1.setH(80);
        node1.setF(); // f = 90

        Node node2 = new Node(0, 1, 0);
        node2.setG(5);
        node2.setH(5);
        node2.setF(); // f = 10 but added first to heap

        Node node3 = new Node(1, 0, 0);
        node3.setG(20);
        node3.setH(10);
        node3.setF(); // f = 30 but has different coordinates

        Node node4 = new Node(1, 1, 0);
        node4.setG(15);
        node4.setH(5);
        node4.setF(); // f = 20

        Node node5 = new Node(2, 1, 0);
        node5.setG(15);
        node5.setH(50);
        node5.setF(); // f = 65

        Node node6 = new Node(1, 2, 0);
        node6.setG(15);
        node6.setH(40);
        node6.setF(); // f = 55

        Node node7 = new Node(2, 2, 0);
        node7.setG(10);
        node7.setH(30);
        node7.setF(); // f = 40

        // Adding nodes to the heap
        heap.add(node1);
        heap.add(node2);
        heap.add(node3);
        heap.add(node4);
        heap.add(node5);
        heap.add(node6);
        heap.add(node7);

        System.out.println("Heap after additions:");
        heap.printHeap();

        // Removing nodes and testing the integrity of the heap after each removal
        System.out.println("\nRemoving nodes:");
        while (heap.getSize() > 0) {
            Node removed = heap.remove();
            System.out.println("Removed: " + removed + ", New Heap: ");
            heap.printHeap();
        }
    }

    //get start and goal nodes from user
    public static int[] getStartGoal(Node[][] world) {
        Scanner s = new Scanner(System.in);
        int[] coordinates = new int[4];

        //loop to get start and goal nodes
        for (int i = 0; i < 4; i++) {
            //ask for user input based on which node is being entered
            if (i == 0) {
                System.out.println("Enter start row (0-" + (size - 1) + "): ");
            } else if (i == 1) {
                System.out.println("Enter start col (0-" + (size - 1) + "): ");
            } else if (i == 2) {
                System.out.println("Enter goal row (0-" + (size - 1) + "): ");
            } else {
                System.out.println("Enter goal col (0-" + (size - 1) + "): ");
            }

            //check if input is an integer
            if (s.hasNextInt()) {
                int input = s.nextInt();
                //check if input is within bounds
                if (input < 0 || input > size - 1) {
                    System.out.println(ANSI_RED + "Error: You did not enter an integer between 0 and " + (size - 1) + "." + ANSI_RESET);
                    exit(1);
                } else {
                    coordinates[i] = input;
                }
            } else {
                System.out.println(ANSI_RED + "Error: You did not enter an integer." + ANSI_RESET);
                exit(1);
            }

            //check if start and goal nodes are traversable
            if (i == 1 && world[coordinates[0]][coordinates[1]].getType() == 1) {
                System.out.println(ANSI_RED + "Error: Start node is not traversable." + ANSI_RESET);
                exit(1);
            } else if (i == 3 && world[coordinates[2]][coordinates[3]].getType() == 1) {
                System.out.println(ANSI_RED + "Error: Goal node is not traversable." + ANSI_RESET);
                exit(1);
            }
        }

        return coordinates;
    }

    //main function
    public static void main(String[] args) {
        //testHeap();
        //generate world and print
        Node[][] world = generateWorld();
        printLegend();
        System.out.println("Starting state:");
        printWorld(world);

        //get start and goal nodes from user
        int[] coordinates = getStartGoal(world);
        int startRow = coordinates[0];
        int startCol = coordinates[1];
        int goalRow = coordinates[2];
        int goalCol = coordinates[3];

        //create start and goal nodes
        Node start = new Node(startRow, startCol, 0);
        Node goal = new Node(goalRow, goalCol, 0);

        //solve for path
        solve(start, goal, world);

        //repeat using same world
        //allows user to continue finding paths within the same world
        while (true) {
            System.out.println("Would you like to find another path? (y/n)");
            Scanner s = new Scanner(System.in);
            String input = s.nextLine();
            if (input.equalsIgnoreCase("y") || input.equalsIgnoreCase("yes")) {
                printWorld(world);
                coordinates = getStartGoal(world);
                startRow = coordinates[0];
                startCol = coordinates[1];
                goalRow = coordinates[2];
                goalCol = coordinates[3];

                start = new Node(startRow, startCol, 0);
                goal = new Node(goalRow, goalCol, 0);

                solve(start, goal, world);
            } else if (input.equalsIgnoreCase("n") || input.equalsIgnoreCase("no")) {
                break;
            } else {
                System.out.println(ANSI_RED + "Error: You did not enter 'yes' or 'no'." + ANSI_RESET);
            }
        }
    }
}
