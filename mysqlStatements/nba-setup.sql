CREATE TABLE Positions(
   Pos INT,
	Abbrev VARCHAR(2),
   Position VARCHAR(16)
   );

CREATE TABLE Coaches(
   ID INT,
   FirstName VARCHAR(16),
   LastName VARCHAR(16),
   PRIMARY KEY (Id)
   );

CREATE TABLE Teams(
   ID INT,
   Name VARCHAR(12),
   State CHAR(2),
   City VARCHAR(16),
   CoachID INT,
   Abbrev VARCHAR(3),
   PRIMARY KEY (ID),
   FOREIGN KEY (CoachId) REFERENCES Coaches(Id)
   );



CREATE TABLE Players(
     ID INT,
     FirstName VARCHAR(16),
     LastName VARCHAR(16),
     TeamID INT,
     Position1 INT,
     Position2 INT,
     Height INT,
     Weight INT,
	  UserTeam int,
     PRIMARY KEY (Id),
     FOREIGN KEY (TeamId) REFERENCES Teams(ID),
     FOREIGN KEY (Position1) REFERENCES Positions(Pos),
     FOREIGN KEY (Position2) REFERENCES Positions(Pos)
   );
   
CREATE TABLE Stats(
   PlayerId INT,
   Season INT,
   Age INT,
   TeamId INT,
   Games INT,
   Starts INT,
   Points INT, 
   Assists INT,
   Rebounds INT,
   Steals INT,
   Blocks INT,
   TurnOver INT,
   FGM INT,
   FGA INT,
   3PM INT,
   3PA INT,
   FTM INT,
   FTA INT,
   PRIMARY KEY(PlayerId, Season, TeamId),
   FOREIGN KEY(PlayerId) REFERENCES Players(ID),
   FOREIGN KEY(TeamId) REFERENCES Teams(ID)
   );

