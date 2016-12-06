import java.util.ArrayList;

public class Player
{
   private String mName;
   private ArrayList<Athlete> mAthleteList;
   private boolean mCurrentTurn;
   
   public Player(String name)
   {
      mName = name;
      mAthleteList = new ArrayList<>(10);
      mCurrentTurn = false;
   }
   
   public void setName(String name)
   {
      mName = name;
   }
   
   public String getName()
   {
      return mName;
   }
   
   public void addAthlete(Athlete athlete)
   {
      mAthleteList.add(athlete);
   }
   
   public ArrayList<Athlete> getAllAthletes()
   {
      return mAthleteList;
   }
   
   public void setCurrentTurn(boolean turn)
   {
      mCurrentTurn = turn;
   }
   
   public boolean getCurrentTurn()
   {
      return mCurrentTurn;
   }
   
   
}
