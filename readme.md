
I took the view that, on a given day, a boat sighting the same boat more than once constitutes more than one 
sighting. Even if the times are only separated by 15 minutes - it is another sighting. An alternative view is 
that if you see the same boat more than once on the same day then that is only one sighting. The code allows for
both options - this is controlled by 'sighting.justOne' in the application.properties.

(All examples and data that follow use the first option for sightings - i.e multiple sightings)

The table of sightings - 'sighting' - maintains the number of sightings per day on a per boat basis.
i.e. it has the name of the boat (actually name and serial for uniqueness) doing the sighting.
The code has hooks to maintain both the 'sighter' and the 'sightee' and doing this would be quite 
straightforward.
The sightings data is generated based on observations being at the same point in time and also 
being physically close. A simple formula was used to calculate the distance bewteen two lat. long. pairs.
No account was taken of the Earth's curvature. A horizon distance of 7.5 km was based on a heuristic of 
1.17 times the square root of  the height of the eye in feet equals the distance to the horizon in 
nautical miles. Say 12 feet  high - gives around 7.5 km.

The code that generates the sighting data is multi-threaded and executes in less than 4 seconds. 
(See teamSiteings method in class MapUtils).
The code to write the data into  mySQL has not been optimised - doing so is important but really
an exercise in configuration rather than one of coding. 
There are no unit tests. Production code would have unit tests.

The whole application can be run as a local server on port 8080. Entry point is rock7.gis.MainApplication 
It exports several endpoints:

rock7/stats  - race info - how far each team travelled. Who travelled the least and who the most.
The results of this are in file 'stats.txt'.

rock7/sighting/generate - creates the sighting data. This is in file 'data.csv'.

and there are others that just show position data etc.

A number of SQL queries have been run against the sighting data. Please see 'sampleQueries.txt'.


