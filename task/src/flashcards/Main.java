package flashcards;

import java.util.*;

import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.io.PrintWriter;

class flashCard {
    public String      defenition;
    public int         numErrors = 0;
    flashCard( String      Defenition, int err ) {
        defenition = Defenition;
        numErrors = err;
    }
}

class FlashCards {
    protected Map<String, flashCard>   cardToDefinition = new LinkedHashMap<>();
    protected Map<String, String>      definitionToCard = new LinkedHashMap<>();
    protected int                      cardsCount = 0;
    protected ArrayList<String>        log = new ArrayList<String>();
    String                             exportName = new String();

    void FlashOutput( String strOut ) {
        System.out.print( strOut );
        log.add(strOut);
    }

    void toLog( String strOut ) {
        log.add(strOut);
    }

    public void AddCard_Stage4(Scanner scan, int num ) {
        String card = new String();
        String defenition = new String();

        FlashOutput("The card #" + num + ":\n> ");
        while( true ) {
             card = scan.nextLine();
             toLog(card + "\n");
             if (cardToDefinition.containsKey(card)) {
                 FlashOutput("The card \"" + card + "\" already exists. Try again:\n> ");
             } else {
                 break;
             }
         }

        FlashOutput("The definition of the card #" + num + ":\n> ");
        while( true ) {
            defenition = scan.nextLine();
            toLog(defenition + "\n");
            if (definitionToCard.containsKey(defenition)) {
                FlashOutput("The definition \"" + defenition + "\" already exists. Try again:\n> ");
            } else {
                break;
            }
        }

        cardToDefinition.put(card, new flashCard( defenition, 0 ));
        definitionToCard.put(defenition, card);
    }

    public void CheckAllCards_Stage4( Scanner scan ) {
        for (Map.Entry<String, flashCard> entry : cardToDefinition.entrySet()) {
            CheckCard_Stage4(entry.getKey(), entry.getValue().defenition, scan );
        }
    }

    public void CheckCard_Stage4(String  card, String  defenition, Scanner scan ) {
        FlashOutput("Print the definition of \"" +  card + "\":\n> " );
        String def = "";
        if( scan.hasNext()) {
            def = scan.nextLine();
            toLog(def + "\n");
        }

        if( defenition.equalsIgnoreCase(def) ) {
            FlashOutput("Correct answer.\n");
        } else {
            flashCard flCard = cardToDefinition.get(card);
            flCard.numErrors++;
            //FlashOutputf("Wrong answer. The correct one is \"" + defenition + "\"," );
            if(definitionToCard.containsKey(def) ) {
                FlashOutput("Wrong answer. The correct one is \"" + defenition + "\", you've just written the definition of \"" + definitionToCard.get(def) + "\".\n" );
            } else {
                FlashOutput("Wrong answer. The correct one is \"" + defenition + "\".\n" );
            }
        }
    }

    public void ProcessFleshCards_Stage4( Scanner scan, String fileExport ) {
        FlashOutput("Input the number of cards:\n> ");
        int cardCount = scan.nextInt();
        if( scan.hasNextLine())
            scan.nextLine();
        for( int i = 1 ; i <= cardCount; ++i ) {
            AddCard_Stage4( scan, i );
        }

        CheckAllCards_Stage4(scan);

    }

    public  void Import(Scanner scan )  {
        FlashOutput("File name:\n> ");

        String fileName = scan.nextLine();
        toLog(fileName + "\n");

        int numCard = Import(fileName );
        if( numCard == 0 )
            FlashOutput("0 cards have been loaded. File not found.\n");
        else
            FlashOutput(numCard + " cards have been loaded.\n");
    }

    public  int Import(String fileName )  {

        File file = new File(fileName);
        int numCard = 0;
        /*if( !file.exists() ) {
            //FlashOutput("0 cards have been loaded. File not found.\n");
            return 0;
        } */
        try {
            String[] strNums = Files.readAllLines(Paths.get(fileName)).toArray(new String[0] );

            for( int  i= 0 ; i < strNums.length ; i += 2  ) {
                if( i+1 < strNums.length ) {
                    String[] strs = strNums[i + 1].split(" ");
                    if( !cardToDefinition.containsKey(strNums[i]) ) {
                        cardToDefinition.put(strNums[i], new flashCard(strs[0], Integer.parseInt(strs[1])));
                        definitionToCard.put(strs[0], strNums[i]);
                    } else {
                        String def = cardToDefinition.get(strNums[i]).defenition;
                        if( def.startsWith("UpdateMeFromImport") ) {
                            int numErr = cardToDefinition.get(strNums[i]).numErrors;
                            definitionToCard.remove(def);
                            definitionToCard.put(strs[0], strNums[i]);
                            cardToDefinition.replace(strNums[i], new flashCard(strs[0], numErr ) );
                        }
                    }
                    numCard++;
                }
            }
        }
        catch (IOException e) {
            FlashOutput("Cannot read file: " + e.getMessage() + "\n");
        }
        return numCard;
    }

    public  void Export(Scanner scan )  {
        FlashOutput("File name:\n> ");

        String fileName = scan.nextLine();
        toLog(fileName + "\n");

        Export(fileName );

        FlashOutput(cardToDefinition.size() + " cards have been saved.\n");
    }

    public  int Export(String fileName )  {

        File file = new File(fileName);
        try {   // overwrites the file
            if( !file.exists())
                file.createNewFile();
            PrintWriter writer = new PrintWriter(file);
            for( Map.Entry<String, flashCard> card : cardToDefinition.entrySet() ) {
                writer.println(card.getKey());
                writer.println(card.getValue().defenition + " " +card.getValue().numErrors );
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            FlashOutput("An exception occurs " + e.getMessage());
        }
        return cardToDefinition.size();

    }

    //public FlashCards( int count ) {
    //    cardsCount = count;
    //}

    public void SetCardsCount( int count ) {
        cardsCount = count;
    }

    public void AddCard(Scanner scan ) {

        FlashOutput("The card:\n> ");

        String card = scan.nextLine();
        toLog(card + "\n");
        if(cardToDefinition.containsKey(card)) {
            FlashOutput("The card \"" + card + "\" already exists.\n");
            return;
        }

        FlashOutput("The definition of the card:\n> ");
        String  defenition = scan.nextLine();
        toLog(defenition + "\n");
        if(definitionToCard.containsKey(defenition)) {
            FlashOutput("The definition \"" + defenition + "\" already exists.\n");
            return;
        }

        cardToDefinition.put(card,  new flashCard( defenition, 0 ));
        definitionToCard.put(defenition, card);
        FlashOutput("The pair (\"" + card +"\":\"" +defenition + "\") has been added.\n");

    }



    public void CheckCard(String  card, String  defenition, Scanner scan ) {
        FlashOutput("Print the definition of \"" +  card + "\":\n> " );
        String def = "";
        if( scan.hasNext()) {
            def = scan.nextLine();
            toLog(def + "\n");
        }

        if( defenition.equalsIgnoreCase(def) ) {
            FlashOutput("Correct answer.\n");
        } else {
            flashCard flCard = cardToDefinition.get(card);
            flCard.numErrors++;
            //FlashOutputf("Wrong answer. The correct one is \"" + defenition + "\"," );
            if(definitionToCard.containsKey(def) ) {
                FlashOutput("Wrong answer. The correct one is \"" + defenition + "\", you've just written the definition of \"" + definitionToCard.get(def) + "\".\n" );
            } else {
                //FlashOutput("Нет определения : " + def + "\n");
                FlashOutput("Wrong answer. The correct one is \"" + defenition + "\".\n" );
            }
        }
    }

    public void Remove(Scanner scan ) {
        FlashOutput("The card:\n> ");

        String card = scan.nextLine();
        toLog(card + "\n");
        if(cardToDefinition.containsKey(card)) {
            String def = cardToDefinition.get(card).defenition;
            cardToDefinition.remove(card);
            definitionToCard.remove(def);
            FlashOutput("The card has been removed.\n");
        } else {
            FlashOutput("Can't remove \"" + card + "\": there is no such card.\n");
        }

    }

    public void Ask(Scanner scan ) {
        FlashOutput("How many times to ask?\n> ");
        int num = scan.nextInt();
        toLog(num + "\n");
        if( scan.hasNextLine() )
            scan.nextLine();

        Set<Map.Entry<String, flashCard>> set = cardToDefinition.entrySet();
        int sz = set.size();
        Random rand = new Random();
        for( int i = 0 ; i < num ; ++i ) {
            int pos = rand.nextInt(sz);
            int j = 0;
            for (Map.Entry<String, flashCard> entry : set ) {
                if( j == pos ) {
                    CheckCard(entry.getKey(), entry.getValue().defenition, scan);
                    break;
                }
                j++;
            }
        }
    }


    void Log(Scanner scan)     {
        FlashOutput("File name:\n> ");
        String fileName = scan.nextLine();
        toLog(fileName + "\n");

        File file = new File(fileName);
        try {   // overwrites the file
            if( !file.exists())
                file.createNewFile();
            PrintWriter writer = new PrintWriter(file);
            for( String str : log ) {
                writer.println(str);
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            FlashOutput("An exception occurs " + e.getMessage());
        }
        FlashOutput("The log has been saved.\n");
    }

    void hardestCard() {
        int maxErr = 0;
        Set<Map.Entry<String, flashCard>> set = cardToDefinition.entrySet();
        for (Map.Entry<String, flashCard> card : set ) {
            maxErr = Math.max(card.getValue().numErrors, maxErr);
        }
        if( maxErr > 0 ) {
            String strOut = new String();//"The hardest cards are";
            int count = 0;
            for (Map.Entry<String, flashCard> card : set) {
                if (maxErr == card.getValue().numErrors) {
                    strOut += String.format(" \"%s\" ,", card.getKey() );
                    count++;
                }
            }
            strOut = strOut.substring(0,strOut.length()-2);
            if( count > 1 )
                strOut = "The hardest cards are" + strOut;
            else
                strOut = "The hardest card is" + strOut;
            strOut += String.format(". You have %d errors answering them.\n", maxErr );
            FlashOutput(strOut);
        } else {
            FlashOutput("There are no cards with errors.\n");
        }
    }


    void Reset()  {
        Set<Map.Entry<String, flashCard>> set = cardToDefinition.entrySet();
        for (Map.Entry<String, flashCard> card : set ) {
            card.getValue().numErrors = 0;
        }
        FlashOutput("Card statistics has been reset.\n");
     }


    public void ProcessFleshCards( Scanner scan, String  name ) {
        boolean stop = false;
        exportName = name;
        while( !stop ) {
            FlashOutput("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):\n");
            FlashOutput("> ");
            String command = scan.nextLine();
            toLog(command + "\n");
            switch ( command ) {
                case "add" :        //  add
                    AddCard( scan );
                    break;
                case "remove" :        //  remove
                    Remove(scan);
                    break;
                case "import" :        //   import
                    Import(scan);
                    break;
                case "export" :        //   export
                    Export(scan);
                    break;
                case "ask" :        //   ask
                    Ask(scan);
                    break;
                case "exit" :        //   exit
                    stop = true;
                    FlashOutput("Bye bye!\n");
                    if( !exportName.isEmpty()) {
                        System.out.println(Export(exportName) + " cards have been saved.");
                    }
                    break;
                case "log" :
                    Log(scan);
                    break;
                case "hardest card" :
                    hardestCard();
                    break;
                case "reset stats" :
                    Reset();
                    break;
            }

        }

    }
}


public class Main {
    // protected final static Scanner scan = new Scanner(System.in).useDelimiter("\n");

    static FlashCards flashCards = new FlashCards();

   /* public static void outputCard( String card, String defenition ) {
        FlashOutput("Card:\n%s \nDefinition:\n%s\n", card, defenition);
    }*/


    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        String fileImport = new String();
        String fileExport = new String();

        for  ( int i = 0 ; i < args.length -1 ; ++i) {
            if("-import".equals(args[i])) {
                fileImport = args[++i];
                System.out.println(flashCards.Import(fileImport) + " cards have been loaded.");
            } else if("-export".equals(args[i])) {
                fileExport = args[++i];
            }
        }

        flashCards.ProcessFleshCards(scan, fileExport);
    }
}
