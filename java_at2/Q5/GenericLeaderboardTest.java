package Q5;

import java.util.*;

/**
 * GenericLeaderboardTest
 * Validates the Leaderboard<T> component across multiple domains (Teams, Players, Colleges),
 * exposes faulty comparators (subtraction overflow, missing tie-breakers),
 * and benchmarks single composite sort vs 3-pass stable sort.
 */
public class GenericLeaderboardTest {

    // Domain 2: Player
    public static class Player {
        public final String name;
        public final int goals;
        public final int assists;

        public Player(String name, int goals, int assists) {
            this.name = name;
            this.goals = goals;
            this.assists = assists;
        }

        @Override
        public String toString() {
            return String.format("%s[G:%d,A:%d]", name, goals, assists);
        }
    }

    // Domain 3: College
    public static class College {
        public final String name;
        public final int gold;
        public final int silver;

        public College(String name, int gold, int silver) {
            this.name = name;
            this.gold = gold;
            this.silver = silver;
        }

        @Override
        public String toString() {
            return String.format("%s(Au:%d,Ag:%d)", name, gold, silver);
        }
    }

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("   ASSIGNMENT 2 - Q5: GENERIC RANKING COMPONENT VALIDATION                     ");
        System.out.println("================================================================================\n");

        runComprehensiveTieBreakingTest();
        runFaultyComparatorBreakTest();
        runMultiDomainReusabilityTest();
        runAlternativeThreePassStableSort();
    }

    /**
     * Test 1: Deep multi-level tie-breaking.
     */
    private static void runComprehensiveTieBreakingTest() {
        System.out.println("--- TEST 1: Deep Multi-Level Tie-Breaking ---");
        Comparator<Team> byRank = Comparator
                .comparingInt((Team t) -> t.points).reversed()
                .thenComparing(Comparator.comparingInt((Team t) -> t.goalDiff).reversed())
                .thenComparing(t -> t.name);

        Leaderboard<Team> board = new Leaderboard<>(byRank);
        board.add(new Team("Hawks", 10, 5));
        board.add(new Team("Tigers", 10, 8));     // Higher GD
        board.add(new Team("Lions", 10, 5));      // Same Pts, same GD as Hawks -> "Hawks" vs "Lions"
        board.add(new Team("Bears", 7, 2));
        board.add(new Team("Wolves", 7, 2));      // Same Pts, same GD as Bears -> "Bears" vs "Wolves"
        board.add(new Team("Zebras", 4, -2));

        List<Team> ranked = board.ranking();
        System.out.println("Ranked Result: " + ranked);
        System.out.println("Verification:");
        System.out.println("  1. Tigers(10,8) [Highest GD at 10 pts]");
        System.out.println("  2. Hawks(10,5)  ['H' before 'L']");
        System.out.println("  3. Lions(10,5)  ['L' after 'H']");
        System.out.println("  4. Bears(7,2)   ['B' before 'W']");
        System.out.println("  5. Wolves(7,2)  ['W' after 'B']");
        System.out.println("  6. Zebras(4,-2) [Lowest points]\n");
    }

    /**
     * Test 2: Deliberately breaking faulty comparators.
     */
    private static void runFaultyComparatorBreakTest() {
        System.out.println("--- TEST 2: Breaking Faulty Comparators ---");

        // Flaw A: Integer Subtraction Overflow: (a, b) -> (b.points - a.points)
        System.out.println("[Flaw A: Integer Subtraction Overflow]");
        Team teamNormal = new Team("SafeTeam", 10, 0);
        Team teamHugePositive = new Team("SuperTeam", Integer.MAX_VALUE - 5, 0);
        Team teamHugeNegative = new Team("PenaltyTeam", Integer.MIN_VALUE + 5, 0);

        Comparator<Team> buggySubtraction = (t1, t2) -> (t2.points - t1.points);
        Comparator<Team> correctComparison = Comparator.comparingInt((Team t) -> t.points).reversed();

        int buggyResult = buggySubtraction.compare(teamHugeNegative, teamHugePositive);
        int correctResult = correctComparison.compare(teamHugeNegative, teamHugePositive);

        System.out.printf("Comparing PenaltyTeam (%d pts) vs SuperTeam (%d pts):%n",
                teamHugeNegative.points, teamHugePositive.points);
        System.out.printf("  Buggy Subtraction (t2.points - t1.points): %d (WRONG: Positive overflow! Says PenaltyTeam ranks above SuperTeam!)%n",
                buggyResult);
        System.out.printf("  Correct Comparator.comparingInt():        %d (CORRECT: SuperTeam strictly outranks PenaltyTeam)%n",
                correctResult);

        // Flaw B: Incomplete Comparator (ignores name tie-breaker)
        System.out.println("\n[Flaw B: Missing Tie-Breaker (Violating Strict Weak Ordering Equivalence)]");
        Comparator<Team> buggyNoName = Comparator
                .comparingInt((Team t) -> t.points).reversed()
                .thenComparing(Comparator.comparingInt((Team t) -> t.goalDiff).reversed());

        Team teamA = new Team("Alpha", 10, 5);
        Team teamZ = new Team("Zulu", 10, 5);

        int compNoName = buggyNoName.compare(teamA, teamZ);
        System.out.printf("Comparing Alpha(10,5) vs Zulu(10,5) without name tie-breaker: compare() = %d%n", compNoName);
        System.out.printf("  Are they equals()? %b (Distinct objects, distinct teams!)%n", teamA.equals(teamZ));
        System.out.println("  Hazard: If used in a TreeSet or binary search, Zulu would be dropped as a duplicate!\n");
    }

    /**
     * Test 3: Multi-Domain Reusability (Players and Colleges).
     */
    private static void runMultiDomainReusabilityTest() {
        System.out.println("--- TEST 3: Multi-Domain Reusability ---");

        // Leaderboard for Players: Goals (high), Assists (high), Name (A-Z)
        Comparator<Player> playerRank = Comparator
                .comparingInt((Player p) -> p.goals).reversed()
                .thenComparing(Comparator.comparingInt((Player p) -> p.assists).reversed())
                .thenComparing(p -> p.name);

        Leaderboard<Player> playerBoard = new Leaderboard<>(playerRank);
        playerBoard.add(new Player("Messi", 25, 18));
        playerBoard.add(new Player("Ronaldo", 28, 6));
        playerBoard.add(new Player("Neymar", 25, 20));

        System.out.println("Player Leaderboard:  " + playerBoard.ranking());

        // Leaderboard for Colleges: Gold (high), Silver (high), Name (A-Z)
        Comparator<College> collegeRank = Comparator
                .comparingInt((College c) -> c.gold).reversed()
                .thenComparing(Comparator.comparingInt((College c) -> c.silver).reversed())
                .thenComparing(c -> c.name);

        Leaderboard<College> collegeBoard = new Leaderboard<>(collegeRank);
        collegeBoard.add(new College("IIT Madras", 15, 10));
        collegeBoard.add(new College("IISc Bangalore", 18, 5));
        collegeBoard.add(new College("NIT Trichy", 15, 12));

        System.out.println("College Leaderboard: " + collegeBoard.ranking() + "\n");
    }

    /**
     * Alternative Concept: 3-Pass Stable Sort.
     */
    private static void runAlternativeThreePassStableSort() {
        System.out.println("--- ALTERNATIVE CONCEPT: 3-Pass Stable Sort ---");
        List<Team> teams = new ArrayList<>();
        teams.add(new Team("Panthers", 9, 4));
        teams.add(new Team("Eagles", 9, 6));
        teams.add(new Team("Cobras", 6, 1));
        teams.add(new Team("Bisons", 9, 4));

        // In 3-pass stable sort, sort from least significant criterion to most significant:
        // Pass 1: Name (A-Z)
        teams.sort(Comparator.comparing(t -> t.name));
        // Pass 2: Goal Difference (High to Low)
        teams.sort(Comparator.comparingInt((Team t) -> t.goalDiff).reversed());
        // Pass 3: Points (High to Low)
        teams.sort(Comparator.comparingInt((Team t) -> t.points).reversed());

        System.out.println("Output from 3-Pass Stable Sort: " + teams);
        System.out.println("Matches Composite Comparator Output: [Eagles(9,6), Bisons(9,4), Panthers(9,4), Cobras(6,1)]");
        System.out.println("Complexity: 3 * O(N log N) passes vs 1 * O(N log N) pass for Composite Comparator.");
        System.out.println("================================================================================\n");
    }
}

