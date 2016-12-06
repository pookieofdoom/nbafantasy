
public class Athlete
{
   private String mFirstName;
   private String mLastName;
   private String mPosition;
   private int mWeightedStats;
   //add stat attributes here later
   public Athlete(String firstName, String lastName, String position, int weightedStats)
   {
      mFirstName = firstName;
      mLastName = lastName;
      mPosition = position;
      mWeightedStats = weightedStats;
   }
   
   public String getFirstName()
   {
      return mFirstName;
   }
   
   public String getLastName()
   {
      return mLastName;
   }
   
   public String getPosition()
   {
      return mPosition;
   }
   
   public int getStats()
   {
      return mWeightedStats;
   }
}
