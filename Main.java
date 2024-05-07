import java.util.Scanner;

public class Main {
	
	public static void main(String[] args) {
		String[] roundCategories = {"Ones", "Twos", "Threes", "Fours", "Fives", "Sixes", "Sequence 20"};
		
		
		
		int numberOfRounds = 7;
		Scanner in = new Scanner(System.in);
		int option = 0;
		
		do {
			int[][] playerScores = new int[2][7];
			playerScores[0] = new int[7];
			playerScores[1] = new int[7];
			boolean[][] categoriesPlayed = new boolean[2][7];

			printScoreTable(roundCategories, playerScores, categoriesPlayed);
			
			System.out.println("Strategic Dice Game\n");
			
			option = getIntegerInput(in, "Play game (1) or Exit game (0) > ");
			
			if (option == 0) {
				break;
			}
			
			System.out.println("\n\n");
			char option2 = ' ';
			for (int round = 0; round < numberOfRounds; round++) {
				System.out.println("---------");
				System.out.println("Round " + (round + 1));
				System.out.println("---------\n\n");
				
				
				for (int player = 1; player <= 2; player++) {
					int remainingDice = 5;
					int category = 0;
					int[] dices = null;
					int[] dicesKept =  {-1, -1, -1, -1, -1};
					int totalMatches = 0;
					boolean categorySelected = false;
					for (int turn = 1; turn <= 3; turn++) {
						if (turn == 1) {
							System.out.printf("First throw of this turn, Player %d to throw 5 dice.\n", player);

						} else {
							System.out.printf("\nNext throw of this turn, Player %d to throw %d\n", player, remainingDice);
						}
						System.out.printf("Throw %d dice, enter 't' to throw > ", remainingDice);
						option2 = in.nextLine().charAt(0);
						
						if (option2 == 't') {
							System.out.printf("\n%d throws remaining for this turn.\n\n", 3 - turn);
							
							dices = throwDice(remainingDice);
							System.out.print("Throw: ");
							printDices(dices);
						}
						
						
						if (!categorySelected) {
							if (turn == 3) {
								category = getValidCategory(in, categoriesPlayed, player);
								System.out.printf("%s selected.\n", roundCategories[category - 1]);
								categorySelected = true;
							} else {
								System.out.print("Enter 's' to select category (number on die/dice) or 'd' to defer > ");
								option2 = in.nextLine().charAt(0);
								
								if (option2 == 's') {
									category = getValidCategory(in, categoriesPlayed, player);
									System.out.printf("%s selected.\n", roundCategories[category - 1]);

									categorySelected = true;

								} else {
									System.out.println("Selection deferred.");
								}
							}		
							
							if (categorySelected) {
								categoriesPlayed[player - 1][category - 1] = true;
							}
						}
						if (categorySelected) {
							if (category != 7) {
								int matchCount = countMatchingDie(category, dices);
								
								if (matchCount > remainingDice) {
									addDice(dicesKept, category, remainingDice);
									totalMatches += remainingDice;
									remainingDice -= remainingDice;
									System.out.printf("That throw had %d dice with value %d. Setting aside %d dice: ", remainingDice, category, matchCount);
									printDices(category, remainingDice);
								} else {
									addDice(dicesKept, category, matchCount);
									totalMatches += matchCount;
									remainingDice -= matchCount;
									System.out.printf("That throw had %d dice with value %d. Setting aside %d dice: ", matchCount, category, matchCount);
									printDices(category, matchCount);
								}
								
								if (turn == 3) {
									int totalScore = totalMatches * category;
									playerScores[player - 1][category - 1] = totalScore;
									printScoreTable(roundCategories, playerScores, categoriesPlayed);
									System.out.printf("Player %d made %d with value %d and scores %d for that round.\n\n\n\n", player, totalMatches, category, totalScore);
								}
							} else {
								System.out.println("0. None");
								for (int i = 0; i < dices.length; i++) {
									System.out.printf("%d. [ %d ]\n", i + 1, dices[i]);
								}
								System.out.print("\nEnter which dice you want to set aside using the number labels separated by space (e.g., 1, 3, 5) or enter 0 for none > ");
								
								int index = 0;
								String line = in.nextLine();
								if (!line.equals("0")) {
									String[] strArr = line.split(" ");
									int[] numbersArr = new int[strArr.length];
									for (int i = 0; i < strArr.length; i++) {
										index = Integer.parseInt(strArr[i]);
										addDice(dicesKept, dices[index - 1], 1);
										numbersArr[i] = dices[index - 1];
									}
									sort(numbersArr);
									remainingDice -= numbersArr.length;
									System.out.println("You have selected the follwing dice to keep.");
									printDices(numbersArr);
								}
								
								if (turn == 3 && !isSequence(dicesKept)) {
									System.out.println("A correct sequence has not been established.");
									System.out.println("Player scores 0 for this category.");
									printScoreTable(roundCategories, playerScores, categoriesPlayed);
									System.out.printf("Player %d did not make a sequence and scores 0 for that round.\n\n\n\n", player);
									break;
								} else if (isSequence(dicesKept)) {
									System.out.println("A correct sequence was established.");
									System.out.println("Player scores 20 for this category.");
									printScoreTable(roundCategories, playerScores, categoriesPlayed);
									System.out.printf("Player %d made a sequence and scores 20 for that round.\n\n\n\n", player);
									break;
								}
							}
							
						}
						
					}
					
					
				}
			}
			System.out.println("\nGame Over.");
		} while (option != 0);
		
		in.close();
	}
	
	public static int getIntegerInput(Scanner in, String prompt) {
		System.out.print(prompt);
		int value = in.nextInt();
		in.nextLine();
		return value;
	}
	
	public static int getValidCategory(Scanner in, boolean[][] categoriesPlayed, int player) {
		int category = 0;
		do {
			System.out.println("Select category to play\n");
			category = getIntegerInput(in, "Ones (1) Twos (2) Threes (3) Fours (4) Fives (5) Sixes (6) or Sequence (7) > ");
			if (categoriesPlayed[player - 1][category - 1]) {
				System.out.println("That category has already been played, select a category that has not been played yet.");
			}
		} while (categoriesPlayed[player - 1][category - 1]);
		return category;
	}
	
	public static void addDice(int[] dicesKept, int value, int count) {
		for (int i = 0; i < count; i++) {
			
			int j = 0;
			while (true) {
				if (dicesKept[j] == -1) {
					dicesKept[j] = value;
					break;
				}
				j++;
			}
		}
	}
	
	public static void printScoreTable(String[] roundCategories, int[][] playerScores, boolean[][] categoriesPlayed) {
		System.out.println("-------------------------------------------");
		System.out.printf("|%-15s|%10s  |%10s  |\n", "", "Player 1", "Player 2");
		System.out.println("-------------------------------------------");
		int playerOneTotal = 0;
		int playerTwoTotal = 0;
		String playerOneScoreStr;
		String playerTwoScoreStr;
		for (int i = 0; i < roundCategories.length; i++) {
			if (!categoriesPlayed[0][i]) {
				playerOneScoreStr = "";
			} else {
				playerOneScoreStr = playerScores[0][i] + "";
			}
			if (!categoriesPlayed[1][i]) {
				playerTwoScoreStr = "";
			} else {
				playerTwoScoreStr = playerScores[1][i] + "";
			}
			System.out.printf("|%-15s|%7s     |%7s     |\n", "" + roundCategories[i],  playerOneScoreStr,  playerTwoScoreStr);
			System.out.println("-------------------------------------------");
			if (playerScores[0][i] > 0) {
				playerOneTotal += playerScores[0][i];
			}
			
			if (playerScores[1][i] > 0) {
				playerTwoTotal += playerScores[1][i];
			}
		}
		System.out.printf("|%-15s|%7d     |%7d     |\n", " TOTAL",  playerOneTotal,  playerTwoTotal);
		System.out.println("-------------------------------------------");
	}
	
	public static int rollDice() {
	    return (int) ((Math.random() * (6 - 1)) + 1);
	}
	
	public static int[] throwDice(int diceCount) {
		int[] dices = new int[diceCount];
		for (int i = 0; i < diceCount; i++) {
			dices[i] = rollDice();
		}
		return dices;
	}
	
	public static void printDices(int[] dices) {
		for (int i = 0; i < dices.length; i++) {
			System.out.printf("[ %d ] ", dices[i]);
		}
		System.out.println();
	}
	
	public static void printDices(int dieNumber, int count) {
		for (int i = 0; i < count; i++) {
			System.out.printf("[ %d ] ", dieNumber);
		}
		System.out.println();
	}
	
	public static int countMatchingDie(int dieNumber, int[] dices) {
		int count = 0;
		for (int i = 0; i < dices.length; i++) {
			if (dices[i] == dieNumber) {
				count++;
			}
		}
		return count;
	}
	
	public static void sort(int[] arr) {
		for (int i = 0; i < arr.length - 1; i++) {
			for (int j = i + 1; j < arr.length; j++) {
				if (arr[i] > arr[j]) {
					int temp = arr[i];
					arr[i] = arr[j];
					arr[j] = temp;
				}
			}
		}
	}
	
	public static boolean isSequence(int[] dices) {
		sort(dices);
		
		for (int i = 0; i < dices.length - 1; i++) {
			if (Math.abs(dices[i] - dices[i + 1]) != 1) {
				return false;
			}
		}
		return true;
	}
}
