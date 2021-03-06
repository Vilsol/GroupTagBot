package pro.zackpollard.telegrambot.grouptagbot;

import com.google.gson.Gson;
import lombok.Getter;
import pro.zackpollard.telegrambot.api.TelegramBot;

import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * @author Zack Pollard
 */
public class GroupTagBot {

    private static String API_KEY;

    @Getter
    private final TelegramBot telegramBot;
    @Getter
    private GroupTagListener listener;
    @Getter
    private GroupTagManager manager;
    private static GroupTagBot instance;

    private final static Gson gson = new Gson();

    public static void main(String[] args) {

        //This simply takes the bots API key from the first command line argument sent to the bot.
        //You do not have to retrieve the API key in this way.
        API_KEY = args[0];
        new GroupTagBot();
    }

    public GroupTagBot() {
        System.out.println("1");

        instance = this;

        //This returns a logged in TelegramBot instance or null if the API key was invalid.
        telegramBot = TelegramBot.login(API_KEY);
        //This registers the SpoilerListener Listener to this bot.

        System.out.println("2");
        manager = new GroupTagManager();

        System.out.println("3");
        telegramBot.getEventsManager().register(listener = new GroupTagListener());
        //This method starts the retrieval of updates.
        //The boolean it accepts is to specify whether to retrieve messages
        //which were sent before the bot was started but after the bot was last turned off.
        System.out.println("4");
        telegramBot.startUpdates(false);

        System.out.println("5");
        Scanner scanner = new Scanner(System.in);

        System.out.println("6");
        boolean running = true;
        System.out.println("7");

        while(running) {
            System.out.println("8");

            System.out.print("I");
            System.out.println("Give");
            System.out.print("Up");
            System.out.println("!");

            System.out.print("root@GroupTagBot$ ");
            String input = scanner.nextLine().trim();

            switch(input) {

                case "exit":
                case "shutdown": {
                    System.out.println("Saving and exiting safely...");
                    manager.saveUsernameCache();
                    System.out.println("Saving username cache...");
                    manager.saveTags();
                    System.out.println("Saving tags...");
                    running = false;
                    break;
                }
                case "stats": {

                    System.out.println("Total cached users: " + manager.getUsernameCache().getUsernameCache().size());
                    System.out.println("Total groups: " + manager.getGroupTags().getGroups().size());
                    break;
                }
                case "cleanup": {

                    int emptyUsernames = 0;

                    Set<Long> toCleanup = new HashSet<>();

                    for(Map.Entry<Long, String> entry : manager.getUsernameCache().getUsernameCache().entrySet()) {

                        if(entry.getValue().equals("") || entry.getValue() == null) {

                            ++emptyUsernames;
                            toCleanup.add(entry.getKey());
                        }
                    }

                    for(Long id : toCleanup) {

                        manager.getUsernameCache().getUsernameCache().remove(id);
                    }

                    System.out.println("Usernames cleaned up: " + emptyUsernames);
                    break;
                }
                default: {

                    int spaceChar = input.indexOf(" ");
                    System.out.println("-console: " + (spaceChar != -1 ? input.substring(spaceChar) : input) + ": command not found");
                    break;
                }
            }
        }

        scanner.close();
        System.exit(0);
    }

    public static Gson getGson() {

        return gson;
    }

    public static GroupTagBot getInstance() {
        return instance;
    }
}
