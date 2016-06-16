"A Datalog-based Tool for Schema Evolution in NoSQL Databases", Version 1.1, 12/06/2016
=====================================================================================

GENERAL USAGE NOTES
-------------------------------------------------------------------------------------
- This is a tool for migrating schema changes to NoSQL databases lazily and compare 
  the results (new database state) to the eager approach
- Currently Lazy and Eager Migration only works for demo files integrated in 
  the project (or changes made as it is described in section "BACKGROUND INFORMATION")


GETTING STARTED
-------------------------------------------------------------------------------------
- Copy the directory "data" and the jar file "SchemaEvolution.jar" to your disk
  (because write access is necessary for editing the database json files)
- To start the application open the executable jar file "SchemaEvolution.jar" and click 
  on the button "Start"
- By adding commands to the command prompt you can start Migration and see the 
  different results (for more information have a look at our video, linked in Section 
  "Video")
- Supported commands have the following syntax: e.g.
	- get Player.id=1
	- put Player(4,"Maggie", 100)
	- add Player.points = 100 or add Player.home="Springfield"
	- delete Player.points
	- copy Player.score to Mission where Player.id=Mission.pid
	- move Player.score to Mission where Player.id=Mission.pid
-Notes for commands:
	- the tool only accepts attribute "id" for get commands
	- commands for a kind that doesn't exist will be ignored 
	- the demo json files don't support duplicate attributes. 
	  Improper commands can lead to uncorrect behaviour

BACKGROUND INFORMATION
-------------------------------------------------------------------------------------
 - The data of our tool is based on json files. The files can be found in folder "data" 
   and are divided in working files (on which the migration happens) and initial files 
   (to reset the database state to initial- read only for our tool)
	1. working files:
		- EDBEager.json
		- EDBLazy.json
		- Schema.json
	2. read only files:
		- EDBInitial.json
		- SchemaInitial.json

 - If you want to edit the demo data, please edit it in the writing as well as in the 
   initial files. Please Note: The name "MG" is reserved and shouldn't be used.
 - If you want to add a new entity to an existing schema, please notice that it has to 
   consist at least one attribute (?id) and it has to be added to all EDB-files.
 - If you want to add a new entity with a new kind, this will only work if you add the 
   associated new schema as well (to the schema-files)

Here you can see an example for the necessary structure of the json-files:

1.EDB-files:
[
{"kind":"Player",
"schemaversion":1,
"attributes":{"id":1,"name":"Lisa","score":20},
"ts":1},
{"kind":"Mission",
"schemaversion":1,
"attributes":{"id":1,"title":"go to library","priority":1,"pid":1},
"ts":4}
]

2.Schema-files:
[
{"kind":"Player",
"schemaversion":1,
"attributes":["?id","?name","?score"],
"ts":1},
{"kind":"Mission",
"schemaversion":1,
"attributes":["?id","?title","?priority","?pid"],
"ts":4}
]


VIDEO
-------------------------------------------------------------------------------------
The demo video for this tool is available at: www.tinyurl.com/Migrator


CONTACT
-------------------------------------------------------------------------------------
Katharina Wiech, katharina.wiech@st.oth-regensburg.de

Stephanie Sombach, stephanie.sombach@st.oth-regensburg.de
