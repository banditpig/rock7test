
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
being physically close. A simple formula was used to calculate the distance bewteen two lat long pairs.
No account was taken of the Earth's curvature. A horizon distance of 7.5 km was based on a heuristic of 
1.17 times the square root of  height of eye in feet equals the distance to the horizon in 
nautical miles. Say 12 feet  high - gives around 7.5 km.

The code that generates the sighting data is multi-threaded and executes in less than 4 seconds. 
(See teamSiteings method in class MapUtils)
The code to write the data into  mySQL has not been optimised - doing so is important but really
an exercise in configuration rather than one of coding. 

The whole application can be run as a local server on port 8080. It exports several endpoints.

rock7/stats  - race info - how far each team travelled. Who travelled the least and who the most.
This is in  file 'stats.txt'.

rock7/sighting/generate - creates the sighting data. This is in file 'data.csv'.

and others that just show position data etc.

A number of SQL queries have been run against the sighting data:
---------------------------------------------------------------

The top 10 teams that had the most number of sightings on 2017-11-26.

mysql> select sum(number), team_name, date from sighting  where date='2017-11-26 00:00:00' group  by team_name order by sum(number) desc limit  10;
+-------------+----------------------+---------------------+
| sum(number) | team_name            | date                |
+-------------+----------------------+---------------------+
|          37 | Njord - 5219         | 2017-11-26 00:00:00 |
|          37 | Trimley Maid - 2355  | 2017-11-26 00:00:00 |
|          14 | Into the Blue - 4722 | 2017-11-26 00:00:00 |
|          14 | Mustique - 5984      | 2017-11-26 00:00:00 |
|           9 | Gregal V - 6318      | 2017-11-26 00:00:00 |
|           8 | Challenger 2 - 5103  | 2017-11-26 00:00:00 |
|           6 | Summer Star - 6292   | 2017-11-26 00:00:00 |
|           6 | Surya - 22302        | 2017-11-26 00:00:00 |
|           6 | Thyra - 6310         | 2017-11-26 00:00:00 |
|           6 | Lothlorien - 5519    | 2017-11-26 00:00:00 |
+-------------+----------------------+---------------------+
10 rows in set (0.04 sec)

Average number of sightings per day.
mysql> select AVG(number), date from sighting group by date order by date ;
+-------------+---------------------+
| AVG(number) | date                |
+-------------+---------------------+
|   2949.2688 | 2017-11-19 00:00:00 |
|     16.8602 | 2017-11-20 00:00:00 |
|      4.1828 | 2017-11-21 00:00:00 |
|      2.0000 | 2017-11-22 00:00:00 |
|      1.4516 | 2017-11-23 00:00:00 |
|      1.1828 | 2017-11-24 00:00:00 |
|      1.1613 | 2017-11-25 00:00:00 |
|      0.9785 | 2017-11-26 00:00:00 |
|      0.3656 | 2017-11-27 00:00:00 |
|      0.3871 | 2017-11-28 00:00:00 |
|      0.4839 | 2017-11-29 00:00:00 |
|      0.5591 | 2017-11-30 00:00:00 |
|      0.2581 | 2017-12-01 00:00:00 |
|      0.1613 | 2017-12-02 00:00:00 |
|      0.3011 | 2017-12-03 00:00:00 |
|      0.2043 | 2017-12-04 00:00:00 |
|      0.2903 | 2017-12-05 00:00:00 |
|      0.5914 | 2017-12-06 00:00:00 |
|      1.1505 | 2017-12-07 00:00:00 |
|      4.5806 | 2017-12-08 00:00:00 |
|      8.1720 | 2017-12-09 00:00:00 |
|     20.2581 | 2017-12-10 00:00:00 |
|      6.6989 | 2017-12-11 00:00:00 |
|      4.6774 | 2017-12-12 00:00:00 |
|      2.9247 | 2017-12-13 00:00:00 |
|      0.7634 | 2017-12-14 00:00:00 |
|      0.8817 | 2017-12-15 00:00:00 |
|      1.1935 | 2017-12-16 00:00:00 |
|      0.0968 | 2017-12-17 00:00:00 |
|      0.0538 | 2017-12-18 00:00:00 |
|      0.0538 | 2017-12-19 00:00:00 |
|      0.0000 | 2017-12-20 00:00:00 |
|      0.0000 | 2017-12-21 00:00:00 |
|      0.0000 | 2017-12-22 00:00:00 |
|      0.0000 | 2017-12-23 00:00:00 |
|      0.0000 | 2017-12-24 00:00:00 |
|      0.0000 | 2017-12-25 00:00:00 |
|      0.0000 | 2017-12-26 00:00:00 |
+-------------+---------------------+
38 rows in set (0.05 sec)

How many sightings did 'Into the Blue' make throughout the race?
mysql> select sum(number) from sighting where team_name like 'Into the Blue%';
+-------------+
| sum(number) |
+-------------+
|        4254 |
+-------------+
1 row in set (0.03 sec)

How many sigtings in total on the last 10 days of the race?
mysql> select sum(number) from sighting where  (date between  '2017-12-17 00:00:00' and '2017-12-26 00:00:00');
+-------------+
| sum(number) |
+-------------+
|          38 |
+-------------+
1 row in set (0.03 sec)

======================================
Here is a copy of the race statistics at GET rock7/stats

{
  "minDist" : 13.891032447888824,
  "maxDist" : 6317.750272385914,
  "avgDist" : 3911.0524364377525,
  "totalDist" : 727455.753177422,
  "minName" : "Njord - 5219",
  "maxName" : "Trident Tides - 4726",
  "nameDist" : {
    "13.891032447888824" : "Njord - 5219",
    "679.0736647318683" : "La Pecadora - 4665",
    "1557.2095745669815" : "Optim'x - 6319",
    "1913.430254927947" : "Pamela - 50671",
    "1967.4079072533902" : "Rapido - 6309",
    "2100.2481410308455" : "NorXL - 22809",
    "2101.1474802859593" : "Wabi - 1136",
    "2238.245538627403" : "Pelican - 6274",
    "2928.640119155882" : "Tapati Nui - 22808",
    "2992.5941939491954" : "Flying Merlin - 6245",
    "3003.6495465109037" : "Angkor - 6147",
    "3034.310785043655" : "Into the Blue - 4722",
    "3094.436489335187" : "Surya - 22302",
    "3125.34944392713" : "Thyra - 6310",
    "3147.9454198401904" : "Balam - 4697",
    "3213.4162737894508" : "Kallima - 5961",
    "3238.5884092211827" : "Moose Of Poole - 5343",
    "3240.9615145319926" : "Just Joia - 22301",
    "3248.1040824776846" : "Raftkin - 5408",
    "3281.0735783643654" : "Selene - 22771",
    "3289.898657833013" : "Cassiopeia - 6313",
    "3295.452323966777" : "Nada - 5653",
    "3336.294577159813" : "Skiathos - 6332",
    "3348.6296654982125" : "Zwerver - 6162",
    "3361.223491362958" : "Ellen - 5468",
    "3363.8326321576833" : "Gregal V - 6318",
    "3377.3660384451227" : "Shimna - 4728",
    "3379.020003600678" : "Lykke - 6256",
    "3395.113280919487" : "Aquafrolic - 3457",
    "3420.0739891849294" : "Region33 - 6085",
    "3427.3446279648497" : "Trimley Maid - 2355",
    "3436.3944413136155" : "Knotty Girl - 5529",
    "3443.750702757536" : "Luna of Castletown - 5157",
    "3448.9484314623073" : "Muttley - 6044",
    "3455.5752344992698" : "Peer Gynt - 5202",
    "3460.614472933225" : "Amarone - 6306",
    "3486.0456388620087" : "Tairua - 4704",
    "3496.891070256952" : "Estrella - 6210",
    "3501.0437600675364" : "Lilith - 6189",
    "3516.6098256587907" : "Libélula - 6045",
    "3531.408470652843" : "Tranquilo - 6226",
    "3550.1005135185874" : "Talulah Ruby II - 5150",
    "3565.6657328021834" : "Infinity of Yar - 6087",
    "3583.7099254786185" : "Santjana - 5311",
    "3583.9708715224874" : "Lei Lei - 6250",
    "3592.674857224987" : "Tommy - 5831",
    "3605.7974458955955" : "Blonde Moment - 4753",
    "3616.5150640197744" : "KALU' III - 6291",
    "3620.6060601451327" : "Togina - 5687",
    "3621.4022408123924" : "Summer Star - 6292",
    "3621.56854805118" : "Alicia - 6160",
    "3644.8570071516183" : "Hot Stuff - 6176",
    "3646.9062802580065" : "Sequentia - 5301",
    "3664.7601719056684" : "Indian Summer - 6153",
    "3669.385988794121" : "Cheri - 6074",
    "3669.6960428221355" : "Aava - 4759",
    "3687.843553403482" : "Think Twice - 4751",
    "3689.088336304851" : "Mar-Jolie - 5137",
    "3699.9130681444026" : "Umoya of London - 5039",
    "3702.559197763251" : "Boxcar XLV - 4747",
    "3704.120976876696" : "Aedis - 6243",
    "3704.346087314382" : "Amanda - 5693",
    "3704.66107201001" : "Kapalai - 6156",
    "3708.7147694600367" : "Andrew - 5638",
    "3713.5185484896556" : "Rubikon - 5159",
    "3747.199563670928" : "Aloha - 6172",
    "3752.1778592003116" : "More Amore - 5985",
    "3752.2376520616735" : "Interlude - 4715",
    "3754.551584956944" : "Nutcracker - 5031",
    "3756.5057520440873" : "Pixel - 6240",
    "3757.8211668430263" : "A Noi - 22259",
    "3768.2798204237015" : "Amuse - 50448",
    "3774.3754736850333" : "Kindred Spirit - 21837",
    "3779.365389449057" : "Avel Biz - 6251",
    "3789.275708349775" : "Challenger 2 - 5103",
    "3799.6753904960838" : "Duale - 6216",
    "3807.7316540279494" : "Infinity - 6070",
    "3816.252378309273" : "La Cigale - 6056",
    "3829.442622687797" : "Celeste of Solent - 5119",
    "3833.071159734446" : "Chantana - 6168",
    "3838.652362561436" : "Geronimo - 6181",
    "3840.0302847131634" : "Barefoot - 6229",
    "3844.1740225916315" : "Barracuda of Islay - 4712",
    "3847.7667786151887" : "Aranui - 21569",
    "3847.9005203131906" : "Moondragon - 6312",
    "3867.3901417900374" : "Enigma VIII - 5105",
    "3869.1220882781686" : "Vahine - 4723",
    "3891.4023891224847" : "Blue Bayou - 4782",
    "3892.3593768671294" : "Southern Star - 4786",
    "3895.022441043862" : "NoStress - 6097",
    "3900.3116804884276" : "Juba - 5109",
    "3905.5984127437077" : "Ellen - 4710",
    "3910.084310772806" : "Alamak - 5803",
    "3916.0915972522334" : "Adrienne - 50750",
    "3926.2224168991493" : "Queen Bee - 6107",
    "3926.5247837349793" : "Sissi - 6248",
    "3933.6907926206227" : "Engla - 6260",
    "3941.8252391516558" : "EH01 - 5678",
    "3958.367687934621" : "Challenger 1 - 6238",
    "3959.1014300196925" : "Dew - 6242",
    "3966.3681274749056" : "Gust of Wind - 5084",
    "3977.8317676889133" : "ALBATROS - 6344",
    "3981.6421076033193" : "Chablis - 6179",
    "3994.9104362952535" : "Scarlet Island Girl - 5729",
    "4004.3529370692313" : "Solid White - 6140",
    "4006.4490896480224" : "Alice - 5433",
    "4039.1671166025035" : "Mad Monkey - 50450",
    "4040.548078543496" : "Albatros - 6188",
    "4046.393510378509" : "Emily Morgan - 21070",
    "4056.0599483072474" : "Arruno - 5990",
    "4056.1258080694447" : "a24e - 3596",
    "4071.4507031140956" : "Caliope - 6316",
    "4073.104085118961" : "Lumikki - 6305",
    "4079.734633432127" : "Clare - 3192",
    "4079.9535592216416" : "Anzur - 6300",
    "4080.8059817894323" : "Lothlorien - 5519",
    "4111.009827559251" : "Brag - 6112",
    "4113.146683332325" : "Dante's - 6148",
    "4116.251888549391" : "Djualyn - 6255",
    "4129.14455581757" : "Luna - 6225",
    "4153.070783477976" : "Naylamp - 6203",
    "4157.038360064611" : "Carioca - 6308",
    "4171.801808414608" : "Nikita - 6191",
    "4175.79229809194" : "Pinta - 50670",
    "4191.262425114629" : "Julia - 6020",
    "4195.702708623926" : "Khaleesi - 5979",
    "4259.8528627550795" : "Golok - 6138",
    "4265.4774686322435" : "Shah - 5976",
    "4279.997885588556" : "Blue Top - 5883",
    "4286.015068845052" : "North Star - 6165",
    "4312.333550405972" : "Thunderboat - 5132",
    "4313.704194976614" : "Associate3 - 6105",
    "4315.431134524077" : "X86 - 5386",
    "4321.715701329777" : "Manihi - 6060",
    "4335.361671482195" : "Knot On Call - 6058",
    "4337.650870585426" : "Garbin2 - 6039",
    "4368.633691613231" : "Sunrise - 5977",
    "4369.309706459553" : "Passepartout - 6061",
    "4377.6742614467275" : "Marlin - 6141",
    "4385.187750312537" : "Ngahue IV - 5649",
    "4393.112068013228" : "Saudade - 5224",
    "4400.265696093224" : "Wink - 5265",
    "4402.4475475851505" : "Grey Goose - 50439",
    "4402.764879261512" : "Salsa - 6322",
    "4404.691029989898" : "Althane - 6164",
    "4410.117907366449" : "Micoton 7 - 2982",
    "4414.120837802428" : "Susan Ann II - 5994",
    "4432.148353092391" : "Mischief - 50445",
    "4433.382263404983" : "Talanta - 5292",
    "4434.886637756287" : "Tiffin - 6234",
    "4437.208862426911" : "Guyader Gastronomie - 6182",
    "4437.918889692683" : "Matenka - 6239",
    "4473.966141746274" : "Odessa - 5554",
    "4481.0730147003505" : "Mustique - 5984",
    "4496.894421162219" : "Dorado - 4756",
    "4503.26548875207" : "Sea Candy - 6152",
    "4526.384010530307" : "Victory Cat - 6289",
    "4533.5100775255505" : "Nisida - 6031",
    "4540.716391817347" : "Milanto - 6236",
    "4548.752651016757" : "Quiset - 5515",
    "4581.819807993003" : "Tugela - 6066",
    "4620.6465253133665" : "Jinja - 1436",
    "4628.8970577284035" : "Nereida - 6231",
    "4636.236953949508" : "Lucky Lady - 6038",
    "4638.076285837882" : "Salamander - 6249",
    "4642.2722881564705" : "@teamtigress - 4705",
    "4643.333531423551" : "Pelizeno - 6311",
    "4676.485313155063" : "Pata Negra - 6051",
    "4740.9566362939" : "Athene - 6115",
    "4742.301355721811" : "Mbolo - 4724",
    "4747.4868738814475" : "Quokka 8 - 5645",
    "4784.380274416715" : "Lin Bi Lan - 4664",
    "4814.313208839435" : "Trifon - 5267",
    "4816.879862763396" : "Eva - 5531",
    "4824.924569951989" : "Jua Kali - 3472",
    "4839.638566262657" : "Solair - 6304",
    "4857.946103412833" : "Meltemi - 4757",
    "4958.113720222028" : "Ecover of Skagen - 4659",
    "4963.606700677014" : "Lazy Way - 6193",
    "4990.031961128521" : "Sea Change II - 5000",
    "5061.601627122509" : "Godspeed - 6211",
    "5110.504157365174" : "Saga - 6013",
    "5146.178711814029" : "Jacky X - 6030",
    "5152.182729168396" : "Madelene II - 6259",
    "5720.8567841406475" : "Bob - 4762",
    "6317.750272385914" : "Trident Tides - 4726"
  }
}



