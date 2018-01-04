# README #

## About ##

The [Whereabouts](http://whereaboutslondon.org) web-application allows one to explore clustered regional data about the Greater London Area. This work is a collaborative research and development project between the [Future Cities Catapult](https://futurecities.catapult.org.uk/) and the [Greater London Authority](https://www.london.gov.uk). The London Datastore is a hub for data related activities for the city. With the latest version, users can find, explore and build on over 500 datasets that the city generates, by either downloading datasets, or access through an API . We've used the Datastore's new spatial search functionality to help us extract data for neighbourhoods across the city, and merged that with open data from other sources, including the Food Standards Agency, the Office for National Statistics, Land Registry, OpenStreetMap, Flickr and Transport for London. By bringing information from these different sources together, we can build up a picture of what makes our local areas similar to, and distinct from, each other. We used an algorithm called K-Means clustering to help us find neighbourhoods that have similar characteristics. We grouped them together to form the Whereabouts you can explore on the map.

![example_plot2.png](https://bitbucket.org/repo/KzXaya/images/902990708-example_plot2.png)

# Tutorial #

To view a how-to on our methodology - from data to mapping - please read the step-by-step [tutorial](https://bitbucket.org/fcclab/command-line-k-means-data-clusterer/wiki/Home) on our Wiki. Alternatively, the k-Means clusterer code included in this repository provides code to map the clusters to polygons using R. 

The code residing in this repository executes *k*-Means clustering from the command line, integrating the input features with the clusters using Weka. The code was written for the automation of *k*-Means clustering from the command line but you can manually process the data using Weka's data explorer. We also include an R-script for the quick generation of resulting cluster maps, again normally a process which is manually achieved in Quantum GIS. The R-script requires the shape file geometry and the cluster output and can be found in the *R* sub-directory. Initially, Weka was used to classify the data but R is fully capable of doing clustering itself.

## Attribution and Licensing ##

All code and work committed on this project is subject to the rules and attribution of the [GNU General Public License v3 of 29 June 2007](http://www.gnu.org/licenses/gpl-3.0.txt). GPL is the most widely used free software license and has a strong copyleft requirement. When distributing derived works, the source code of the work must be made available under the same license.

You are required to:

* Disclose Source: Source code must be made available when distributing the software. In the case of LGPL, the source for the library (and not the entire program) must be made available.
* License and copyright notice: Include a copy of the license and copyright notice with the code.
* State Changes: Indicate significant changes made to the code.

Permitted for:

* Commercial Use: This software and derivatives may be used for commercial purposes.
* Distribution: You may distribute this software.
* Modification: This software may be modified.
* Patent Grant: This license provides an express grant of patent rights from the contributor to the recipient.
* Private Use: You may use and modify the software without distributing it.

Forbidden from:

* Hold Liable: Software is provided without warranty and the software author/license owner cannot be held liable for damages.
* Sublicensing: You may not grant a sublicense to modify and distribute this software to third parties not included in the license.


This repository contains the code, assets and methodology used to construct the [Whereabouts London website](http://whereaboutslondon.org).

## Contacts ##

* Future Cities Catapult (https://bitbucket.org/fcclab)

# Usage #

To use the k-Means data clusterer at the command line, execute the following commands.

```
#!console
Usage:
java -jar DataClusterer [statistics_file.csv/arff] [number_of_clusters] [fields_to_filter/exclude]

Example:
Explore attributes: java -jar DataClusterer statistics.csv
Perform clustering: java -jar DataClusterer statistics.csv 8 Lat,Lon,Geography,Geography_Code,Date,Greenspace:_Local_Parks,Cycle_Hire_Loccations_Count,Foodagency:_Establishment_Count,Cycle_Hire_Locations_Count

* Please use an underscore as a space if the feature heading contains a space.
```

Output is sent to two files:
<inputfile>.clusters.csv
<inputfile>.centroids.txt

The clusters extension file contains the original input data with joined cluster ids. While the centroids extension file contains the mean centroids output as it would be reflected in Weka.

To generate a first stage map of your clusters you can use and modify the R script found in the \KMeansDataClusterer\R directory. You'll need to modify the script to point to the relevant shape files and <inputfile>.clusters.csv output file. The R script was written in combination with [R Studio](http://www.rstudio.com) which is a useful R IDE. 


```
#!R

#INSTALL THE FOLLOWING:
#install.packages(c("plyr", "ggplot2","rgeos", "maptools")))

library(rgeos)
library(maptools)
library(gpclib)  # may be needed, may not be
library(plyr)
library(ggplot2)
require(RColorBrewer)

# FUNCTIONS #

# MAIN
#scale set
scale = 8
borderwidth = 0
numclusters = max(clusterdata$Cluster) + 1
# Get the polygon data from the shape file
np_dist <- readShapeSpatial('LSOA_2011_EW_BFE_justLondon.shp')
# Plot the shape file
#plot(np_dist)
# Associate the clusters with the LSOAs
clusterdata <- read.csv("clusters.csv", header=TRUE)
# Do Summary
summary(clusterdata$Cluster)
# Graph Clusters using GGPlot
np_dist <- fortify(np_dist, region = "LSOA11CD")
# Change ids to uppercase
np_dist$id <- toupper(np_dist$id)

#################################
# GENERATE CLUSTER MAP
#################################
# Redirect Output to Write to Disk
png("clustermap.png", width=(1200*scale), height=(798*scale))
# Pallette colours:
ggplot() + geom_map(data = clusterdata, aes(map_id = Geography.Code, fill = Cluster), 
                    colour = "black", size=borderwidth, map = np_dist) + scale_fill_gradientn(colours = brewer.pal( numclusters , "Spectral" )) + expand_limits(x = np_dist$long, y = np_dist$lat) + theme(line = element_blank(), text = element_blank(), title = element_blank(), rect = element_blank())
# Close output
dev.off()

```

# Example Ouput #

```
#!console

java -jar DataClusterer v6.csv 8 Lat,Lon,Geography,Geography_Code,Date,Greenspace:_Local_Parks,Cycle_Hire_Loccations_Count,Foodagency:_Establishment_Count,Cycle_Hire_Locations_Count

```

Print Output

```
#!text

Running k-Means Clustering
Input File: v6.csv
Number of Clusters: 8
Filter Columns: Lat,Lon,Geography,Geography_Code,Date,Greenspace:_Local_Parks,Cycle_Hire_Loccations_Count,Foodagency:_Establishment_Count,Cycle_Hire_Locations_Count
****************************************************************
Loading Data...
---Registering Weka Editors---
Trying to add database driver (JDBC): RmiJdbc.RJDriver - Error, not in CLASSPATH?
Trying to add database driver (JDBC): jdbc.idbDriver - Error, not in CLASSPATH?
Trying to add database driver (JDBC): org.gjt.mm.mysql.Driver - Error, not in CLASSPATH?
Trying to add database driver (JDBC): com.mckoi.JDBCDriver - Error, not in CLASSPATH?
Trying to add database driver (JDBC): org.hsqldb.jdbcDriver - Error, not in CLASSPATH?
****************************************************************
Input Feature List:
index: 1 	 Geography
index: 2 	 Geography Code
index: 3 	 Date
index: 4 	 X
index: 5 	 Y
index: 6 	 Lat
index: 7 	 Lon
index: 8 	 Age: Age 0 to 4; measures: Value
index: 9 	 Age: Age 10 to 14; measures: Value
index: 10 	 Age: Age 15; measures: Value
index: 11 	 Age: Age 16 to 17; measures: Value
index: 12 	 Age: Age 18 to 19; measures: Value
index: 13 	 Age: Age 20 to 24; measures: Value
index: 14 	 Age: Age 25 to 29; measures: Value
index: 15 	 Age: Age 30 to 44; measures: Value
index: 16 	 Age: Age 45 to 59; measures: Value
index: 17 	 Age: Age 5 to 7; measures: Value
index: 18 	 Age: Age 60 to 64; measures: Value
index: 19 	 Age: Age 65 to 74; measures: Value
index: 20 	 Age: Age 75 to 84; measures: Value
index: 21 	 Age: Age 8 to 9; measures: Value
index: 22 	 Age: Age 85 to 89; measures: Value
index: 23 	 Age: Age 90 and over; measures: Value
index: 24 	 Cars: 1 car or van in household; measures: Value
index: 25 	 Cars: 2 cars or vans in household; measures: Value
index: 26 	 Cars: 3 cars or vans in household; measures: Value
index: 27 	 Cars: 4 or more cars or vans in household; measures: Value
index: 28 	 Cars: No cars or vans in household; measures: Value
index: 29 	 Central Heating: Electric (including storage heaters) central heating; measures: Value
index: 30 	 Central Heating: Gas central heating; measures: Value
index: 31 	 Central Heating: No central heating; measures: Value
index: 32 	 Central Heating: Oil central heating; measures: Value
index: 33 	 Central Heating: Other central heating; measures: Value
index: 34 	 Central Heating: Solid fuel (for example wood, coal) central heating; measures: Value
index: 35 	 Central Heating: Two or more types of central heating; measures: Value
index: 36 	 Crime: Anti-social behaviour
index: 37 	 Crime: Burglary
index: 38 	 Crime: Criminal damage and arson
index: 39 	 Crime: Drugs
index: 40 	 Crime: Other crime
index: 41 	 Crime: Other theft
index: 42 	 Crime: Public disorder and weapons
index: 43 	 Crime: Robbery
index: 44 	 Crime: Shoplifting
index: 45 	 Crime: Vehicle crime
index: 46 	 Crime: Violent crime
index: 47 	 Distance travelled to work: 10km to less than 20km; measures: Value
index: 48 	 Distance travelled to work: 20km to less than 30km; measures: Value
index: 49 	 Distance travelled to work: 2km to less than 5km; measures: Value
index: 50 	 Distance travelled to work: 30km to less than 40km; measures: Value
index: 51 	 Distance travelled to work: 40km to less than 60km; measures: Value
index: 52 	 Distance travelled to work: 5km to less than 10km; measures: Value
index: 53 	 Distance travelled to work: 60km and over; measures: Value
index: 54 	 Distance travelled to work: Less than 2km; measures: Value
index: 55 	 Distance travelled to work: Other; measures: Value
index: 56 	 Dwelling Type: Shared dwelling; measures: Value
index: 57 	 Dwelling Type: Unshared dwelling; measures: Value
index: 58 	 Establishment Type: All communal establishments; measures: Value
index: 59 	 Establishment Type: Establishment not stated; measures: Value
index: 60 	 Establishment Type: Medical and care establishment: Local Authority: Care home or other home; measures: Value
index: 61 	 Establishment Type: Medical and care establishment: Local Authority: Children's home (including secure units); measures: Value
index: 62 	 Establishment Type: Medical and care establishment: NHS: General hospital; measures: Value
index: 63 	 Establishment Type: Medical and care establishment: NHS: Mental health hospital/unit (including secure units); measures: Value
index: 64 	 Establishment Type: Medical and care establishment: NHS: Other hospital; measures: Value
index: 65 	 Establishment Type: Medical and care establishment: Other: Care home with nursing; measures: Value
index: 66 	 Establishment Type: Medical and care establishment: Other: Care home without nursing; measures: Value
index: 67 	 Establishment Type: Medical and care establishment: Other: Children's home (including secure units); measures: Value
index: 68 	 Establishment Type: Medical and care establishment: Other; measures: Value
index: 69 	 Establishment Type: Medical and care establishment: Registered Social Landlord/Housing Association; measures: Value
index: 70 	 Establishment Type: Other establishments; measures: Value
index: 71 	 General Health: Bad health; measures: Value
index: 72 	 General Health: Fair health; measures: Value
index: 73 	 General Health: Good health; measures: Value
index: 74 	 General Health: Very bad health; measures: Value
index: 75 	 General Health: Very good health; measures: Value
index: 76 	 Hours Worked: Females: Full-time: 31 to 48 hours worked; measures: Value
index: 77 	 Hours Worked: Females: Full-time: 49 or more hours worked; measures: Value
index: 78 	 Hours Worked: Females: Part-time: 15 hours or less worked; measures: Value
index: 79 	 Hours Worked: Females: Part-time: 16 to 30 hours worked; measures: Value
index: 80 	 Hours Worked: Males: Full-time: 49 or more hours worked; measures: Value
index: 81 	 Hours Worked: Males: Part-time: 15 hours or less worked; measures: Value
index: 82 	 Hours Worked: Males: Part-time: 16 to 30 hours worked; measures: Value
index: 83 	 Hours Worked: Males: Total; measures: Value
index: 84 	 Household Composition: One family only: Cohabiting couple: All children non-dependent; measures: Value
index: 85 	 Household Composition: One family only: Cohabiting couple: Dependent children; measures: Value
index: 86 	 Household Composition: One family only: Cohabiting couple: No children; measures: Value
index: 87 	 Household Composition: One family only: Lone parent: All children non-dependent; measures: Value
index: 88 	 Household Composition: One family only: Lone parent: Dependent children; measures: Value
index: 89 	 Household Composition: One person household: Aged 65 and over; measures: Value
index: 90 	 Household Composition: One person household: Other; measures: Value
index: 91 	 Occupation: 1. Managers, directors and senior officials; Age: Age 16 to 24; measures: Value
index: 92 	 Occupation: 1. Managers, directors and senior officials; Age: Age 25 to 34; measures: Value
index: 93 	 Occupation: 1. Managers, directors and senior officials; Age: Age 35 to 49; measures: Value
index: 94 	 Occupation: 1. Managers, directors and senior officials; Age: Age 50 to 64; measures: Value
index: 95 	 Occupation: 1. Managers, directors and senior officials; Age: Age 65 and over; measures: Value
index: 96 	 Occupation: 2. Professional occupations; Age: Age 16 to 24; measures: Value
index: 97 	 Occupation: 2. Professional occupations; Age: Age 25 to 34; measures: Value
index: 98 	 Occupation: 2. Professional occupations; Age: Age 35 to 49; measures: Value
index: 99 	 Occupation: 2. Professional occupations; Age: Age 50 to 64; measures: Value
index: 100 	 Occupation: 2. Professional occupations; Age: Age 65 and over; measures: Value
index: 101 	 Occupation: 3. Associate professional and technical occupations; Age: Age 16 to 24; measures: Value
index: 102 	 Occupation: 3. Associate professional and technical occupations; Age: Age 25 to 34; measures: Value
index: 103 	 Occupation: 3. Associate professional and technical occupations; Age: Age 35 to 49; measures: Value
index: 104 	 Occupation: 3. Associate professional and technical occupations; Age: Age 50 to 64; measures: Value
index: 105 	 Occupation: 3. Associate professional and technical occupations; Age: Age 65 and over; measures: Value
index: 106 	 Occupation: 4. Administrative and secretarial occupations; Age: Age 16 to 24; measures: Value
index: 107 	 Occupation: 4. Administrative and secretarial occupations; Age: Age 25 to 34; measures: Value
index: 108 	 Occupation: 4. Administrative and secretarial occupations; Age: Age 35 to 49; measures: Value
index: 109 	 Occupation: 4. Administrative and secretarial occupations; Age: Age 50 to 64; measures: Value
index: 110 	 Occupation: 4. Administrative and secretarial occupations; Age: Age 65 and over; measures: Value
index: 111 	 Occupation: 5. Skilled trades occupations; Age: Age 16 to 24; measures: Value
index: 112 	 Occupation: 5. Skilled trades occupations; Age: Age 25 to 34; measures: Value
index: 113 	 Occupation: 5. Skilled trades occupations; Age: Age 35 to 49; measures: Value
index: 114 	 Occupation: 5. Skilled trades occupations; Age: Age 50 to 64; measures: Value
index: 115 	 Occupation: 5. Skilled trades occupations; Age: Age 65 and over; measures: Value
index: 116 	 Occupation: 6. Caring, leisure and other service occupations; Age: Age 16 to 24; measures: Value
index: 117 	 Occupation: 6. Caring, leisure and other service occupations; Age: Age 25 to 34; measures: Value
index: 118 	 Occupation: 6. Caring, leisure and other service occupations; Age: Age 35 to 49; measures: Value
index: 119 	 Occupation: 6. Caring, leisure and other service occupations; Age: Age 50 to 64; measures: Value
index: 120 	 Occupation: 6. Caring, leisure and other service occupations; Age: Age 65 and over; measures: Value
index: 121 	 Occupation: 7. Sales and customer service occupations; Age: Age 16 to 24; measures: Value
index: 122 	 Occupation: 7. Sales and customer service occupations; Age: Age 25 to 34; measures: Value
index: 123 	 Occupation: 7. Sales and customer service occupations; Age: Age 35 to 49; measures: Value
index: 124 	 Occupation: 7. Sales and customer service occupations; Age: Age 50 to 64; measures: Value
index: 125 	 Occupation: 7. Sales and customer service occupations; Age: Age 65 and over; measures: Value
index: 126 	 Occupation: 8. Process, plant and machine operatives; Age: Age 16 to 24; measures: Value
index: 127 	 Occupation: 8. Process, plant and machine operatives; Age: Age 25 to 34; measures: Value
index: 128 	 Occupation: 8. Process, plant and machine operatives; Age: Age 35 to 49; measures: Value
index: 129 	 Occupation: 8. Process, plant and machine operatives; Age: Age 50 to 64; measures: Value
index: 130 	 Occupation: 8. Process, plant and machine operatives; Age: Age 65 and over; measures: Value
index: 131 	 Occupation: 9. Elementary occupations; Age: Age 16 to 24; measures: Value
index: 132 	 Occupation: 9. Elementary occupations; Age: Age 25 to 34; measures: Value
index: 133 	 Occupation: 9. Elementary occupations; Age: Age 35 to 49; measures: Value
index: 134 	 Occupation: 9. Elementary occupations; Age: Age 50 to 64; measures: Value
index: 135 	 Occupation: 9. Elementary occupations; Age: Age 65 and over; measures: Value
index: 136 	 Passports Held: Africa; measures: Value
index: 137 	 Passports Held: Antarctica and Oceania; measures: Value
index: 138 	 Passports Held: British Overseas Territories; measures: Value
index: 139 	 Passports Held: Central America; measures: Value
index: 140 	 Passports Held: Middle East and Asia; measures: Value
index: 141 	 Passports Held: North America and the Caribbean; measures: Value
index: 142 	 Passports Held: Other Europe: EU countries; measures: Value
index: 143 	 Passports Held: Other Europe: Non EU countries; measures: Value
index: 144 	 Passports Held: Republic of Ireland; measures: Value
index: 145 	 Passports Held: South America; measures: Value
index: 146 	 Passports Held: United Kingdom; measures: Value
index: 147 	 Tenure: Living rent free; measures: Value
index: 148 	 Tenure: Owned: Owned outright; measures: Value
index: 149 	 Tenure: Owned: Owned with a mortgage or loan; measures: Value
index: 150 	 Tenure: Owned; measures: Value
index: 151 	 Tenure: Private rented: Other; measures: Value
index: 152 	 Tenure: Private rented: Private landlord or letting agency; measures: Value
index: 153 	 Tenure: Private rented; measures: Value
index: 154 	 Tenure: Shared ownership (part owned and part rented); measures: Value
index: 155 	 Tenure: Social rented: Other; measures: Value
index: 156 	 Tenure: Social rented: Rented from council (Local Authority); measures: Value
index: 157 	 Tenure: Social rented; measures: Value
index: 158 	 Qualification: No qualifications; measures: Value
index: 159 	 Qualification: Level 1 qualifications; measures: Value
index: 160 	 Qualification: Level 2 qualifications; measures: Value
index: 161 	 Qualification: Apprenticeship; measures: Value
index: 162 	 Qualification: Level 3 qualifications; measures: Value
index: 163 	 Qualification: Level 4 qualifications and above; measures: Value
index: 164 	 Qualification: Other qualifications; measures: Value
index: 165 	 Method of Travel to Work: Work mainly at or from home; measures: Value
index: 166 	 Method of Travel to Work: Underground, metro, light rail, tram; measures: Value
index: 167 	 Method of Travel to Work: Train; measures: Value
index: 168 	 Method of Travel to Work: Bus, minibus or coach; measures: Value
index: 169 	 Method of Travel to Work: Taxi; measures: Value
index: 170 	 Method of Travel to Work: Motorcycle, scooter or moped; measures: Value
index: 171 	 Method of Travel to Work: Driving a car or van; measures: Value
index: 172 	 Method of Travel to Work: Passenger in a car or van; measures: Value
index: 173 	 Method of Travel to Work: Bicycle; measures: Value
index: 174 	 Method of Travel to Work: On foot; measures: Value
index: 175 	 Method of Travel to Work: Other method of travel to work; measures: Value
index: 176 	 Method of Travel to Work: Not in employment; measures: Value
index: 177 	 Occupation: 1. Managers, directors and senior officials
index: 178 	 Occupation: 2. Professional occupations
index: 179 	 Occupation: 3. Associate professional and technical occupations
index: 180 	 Occupation: 4. Administrative and secretarial occupations
index: 181 	 Occupation: 5. Skilled trades occupations
index: 182 	 Occupation: 6. Caring, leisure and other service occupations
index: 183 	 Occupation: 7. Sales and customer service occupations
index: 184 	 Occupation: 8. Process, plant and machine operatives
index: 185 	 Occupation: 9. Elementary occupations
index: 186 	 FlickrPhotoCount
index: 187 	 Mean of Medians Weighted by Sale Counts (2009 to 2013)
index: 188 	 Main Language: All people aged 16 and over in household have English as a main language (English or Welsh in Wales); measures: Value
index: 189 	 Main Language: At least one but not all people aged 16 and over in household have English as a main language (English or Welsh in Wales); measures: Value
index: 190 	 Main Language: No people aged 16 and over in household but at least one person aged 3 to 15 has English as a main language (English or Welsh in Wales); measures: Value
index: 191 	 Main Language: No people in household have English as a main language (English or Welsh in Wales); measures: Value
index: 192 	 Foodagency: Establishment Count
index: 193 	 Foodagency: per km squared
index: 194 	 Cycle Hire Locations Count
index: 195 	 Pubs per square km
index: 196 	 NS-SeC: 1. Higher managerial, administrative and professional occupations; Economic Activity: Economically active: In employment: Employee: Part-time (including full-time students); measures: Value
index: 197 	 NS-SeC: 1. Higher managerial, administrative and professional occupations; Economic Activity: Economically active: In employment: Employee: Full-time (including full-time students); measures: Value
index: 198 	 NS-SeC: 1. Higher managerial, administrative and professional occupations; Economic Activity: Economically active: In employment: Self-employed: Part-time (including full-time students); measures: Value
index: 199 	 NS-SeC: 1. Higher managerial, administrative and professional occupations; Economic Activity: Economically active: In employment: Self-employed: Full-time (including full-time students); measures: Value
index: 200 	 NS-SeC: 1. Higher managerial, administrative and professional occupations; Economic Activity: Economically active: Unemployed (including full-time students); measures: Value
index: 201 	 NS-SeC: 1. Higher managerial, administrative and professional occupations; Economic Activity: Economically inactive; measures: Value
index: 202 	 NS-SeC: 2. Lower managerial, administrative and professional occupations; Economic Activity: Economically active: In employment: Employee: Part-time (including full-time students); measures: Value
index: 203 	 NS-SeC: 2. Lower managerial, administrative and professional occupations; Economic Activity: Economically active: In employment: Employee: Full-time (including full-time students); measures: Value
index: 204 	 NS-SeC: 2. Lower managerial, administrative and professional occupations; Economic Activity: Economically active: In employment: Self-employed: Part-time (including full-time students); measures: Value
index: 205 	 NS-SeC: 2. Lower managerial, administrative and professional occupations; Economic Activity: Economically active: In employment: Self-employed: Full-time (including full-time students); measures: Value
index: 206 	 NS-SeC: 2. Lower managerial, administrative and professional occupations; Economic Activity: Economically active: Unemployed (including full-time students); measures: Value
index: 207 	 NS-SeC: 2. Lower managerial, administrative and professional occupations; Economic Activity: Economically inactive; measures: Value
index: 208 	 NS-SeC: 3. Intermediate occupations; Economic Activity: Economically active: In employment: Employee: Part-time (including full-time students); measures: Value
index: 209 	 NS-SeC: 3. Intermediate occupations; Economic Activity: Economically active: In employment: Employee: Full-time (including full-time students); measures: Value
index: 210 	 NS-SeC: 3. Intermediate occupations; Economic Activity: Economically active: In employment: Self-employed: Part-time (including full-time students); measures: Value
index: 211 	 NS-SeC: 3. Intermediate occupations; Economic Activity: Economically active: In employment: Self-employed: Full-time (including full-time students); measures: Value
index: 212 	 NS-SeC: 3. Intermediate occupations; Economic Activity: Economically active: Unemployed (including full-time students); measures: Value
index: 213 	 NS-SeC: 3. Intermediate occupations; Economic Activity: Economically inactive; measures: Value
index: 214 	 NS-SeC: 4. Small employers and own account workers; Economic Activity: Economically active: In employment: Self-employed: Part-time (including full-time students); measures: Value
index: 215 	 NS-SeC: 4. Small employers and own account workers; Economic Activity: Economically active: In employment: Self-employed: Full-time (including full-time students); measures: Value
index: 216 	 NS-SeC: 4. Small employers and own account workers; Economic Activity: Economically active: Unemployed (including full-time students); measures: Value
index: 217 	 NS-SeC: 4. Small employers and own account workers; Economic Activity: Economically inactive; measures: Value
index: 218 	 NS-SeC: 5. Lower supervisory and technical occupations; Economic Activity: Economically active: In employment: Employee: Part-time (including full-time students); measures: Value
index: 219 	 NS-SeC: 5. Lower supervisory and technical occupations; Economic Activity: Economically active: In employment: Employee: Full-time (including full-time students); measures: Value
index: 220 	 NS-SeC: 5. Lower supervisory and technical occupations; Economic Activity: Economically active: In employment: Self-employed: Part-time (including full-time students); measures: Value
index: 221 	 NS-SeC: 5. Lower supervisory and technical occupations; Economic Activity: Economically active: In employment: Self-employed: Full-time (including full-time students); measures: Value
index: 222 	 NS-SeC: 5. Lower supervisory and technical occupations; Economic Activity: Economically active: Unemployed (including full-time students); measures: Value
index: 223 	 NS-SeC: 5. Lower supervisory and technical occupations; Economic Activity: Economically inactive; measures: Value
index: 224 	 NS-SeC: 6. Semi-routine occupations; Economic Activity: Economically active: In employment: Employee: Part-time (including full-time students); measures: Value
index: 225 	 NS-SeC: 6. Semi-routine occupations; Economic Activity: Economically active: In employment: Employee: Full-time (including full-time students); measures: Value
index: 226 	 NS-SeC: 6. Semi-routine occupations; Economic Activity: Economically active: In employment: Self-employed: Part-time (including full-time students); measures: Value
index: 227 	 NS-SeC: 6. Semi-routine occupations; Economic Activity: Economically active: In employment: Self-employed: Full-time (including full-time students); measures: Value
index: 228 	 NS-SeC: 6. Semi-routine occupations; Economic Activity: Economically active: Unemployed (including full-time students); measures: Value
index: 229 	 NS-SeC: 6. Semi-routine occupations; Economic Activity: Economically inactive; measures: Value
index: 230 	 NS-SeC: 7. Routine occupations; Economic Activity: Economically active: In employment: Employee: Part-time (including full-time students); measures: Value
index: 231 	 NS-SeC: 7. Routine occupations; Economic Activity: Economically active: In employment: Employee: Full-time (including full-time students); measures: Value
index: 232 	 NS-SeC: 7. Routine occupations; Economic Activity: Economically active: Unemployed (including full-time students); measures: Value
index: 233 	 NS-SeC: 7. Routine occupations; Economic Activity: Economically inactive; measures: Value
index: 234 	 NS-SeC: 8. Never worked and long-term unemployed; Economic Activity: Economically active: Unemployed (including full-time students); measures: Value
index: 235 	 NS-SeC: 8. Never worked and long-term unemployed; Economic Activity: Economically inactive; measures: Value
index: 236 	 NS-SeC: L15 Full-time students; Economic Activity: Economically active: In employment: Employee: Part-time (including full-time students); measures: Value
index: 237 	 NS-SeC: L15 Full-time students; Economic Activity: Economically active: In employment: Employee: Full-time (including full-time students); measures: Value
index: 238 	 NS-SeC: L15 Full-time students; Economic Activity: Economically active: In employment: Self-employed: Part-time (including full-time students); measures: Value
index: 239 	 NS-SeC: L15 Full-time students; Economic Activity: Economically active: In employment: Self-employed: Full-time (including full-time students); measures: Value
index: 240 	 NS-SeC: L15 Full-time students; Economic Activity: Economically active: Unemployed (including full-time students); measures: Value
index: 241 	 NS-SeC: L15 Full-time students; Economic Activity: Economically inactive; measures: Value
index: 242 	 Greenspace: Local Parks
index: 243 	 Greenspace: Local Parks per square km
Filtering: Lat
Filtering: Lon
Filtering: Geography
Filtering: Geography_Code
Filtering: Date
Filtering: Greenspace:_Local_Parks
Filtering: Foodagency:_Establishment_Count
Filtering: Cycle_Hire_Locations_Count
****************************************************************
Filtered Feature List:
index: 1 	 X
index: 2 	 Y
index: 3 	 Age:_Age_0_to_4;_measures:_Value
index: 4 	 Age:_Age_10_to_14;_measures:_Value
index: 5 	 Age:_Age_15;_measures:_Value
index: 6 	 Age:_Age_16_to_17;_measures:_Value
index: 7 	 Age:_Age_18_to_19;_measures:_Value
index: 8 	 Age:_Age_20_to_24;_measures:_Value
index: 9 	 Age:_Age_25_to_29;_measures:_Value
index: 10 	 Age:_Age_30_to_44;_measures:_Value
index: 11 	 Age:_Age_45_to_59;_measures:_Value
index: 12 	 Age:_Age_5_to_7;_measures:_Value
index: 13 	 Age:_Age_60_to_64;_measures:_Value
index: 14 	 Age:_Age_65_to_74;_measures:_Value
index: 15 	 Age:_Age_75_to_84;_measures:_Value
index: 16 	 Age:_Age_8_to_9;_measures:_Value
index: 17 	 Age:_Age_85_to_89;_measures:_Value
index: 18 	 Age:_Age_90_and_over;_measures:_Value
index: 19 	 Cars:_1_car_or_van_in_household;_measures:_Value
index: 20 	 Cars:_2_cars_or_vans_in_household;_measures:_Value
index: 21 	 Cars:_3_cars_or_vans_in_household;_measures:_Value
index: 22 	 Cars:_4_or_more_cars_or_vans_in_household;_measures:_Value
index: 23 	 Cars:_No_cars_or_vans_in_household;_measures:_Value
index: 24 	 Central_Heating:_Electric_(including_storage_heaters)_central_heating;_measures:_Value
index: 25 	 Central_Heating:_Gas_central_heating;_measures:_Value
index: 26 	 Central_Heating:_No_central_heating;_measures:_Value
index: 27 	 Central_Heating:_Oil_central_heating;_measures:_Value
index: 28 	 Central_Heating:_Other_central_heating;_measures:_Value
index: 29 	 Central_Heating:_Solid_fuel_(for_example_wood,_coal)_central_heating;_measures:_Value
index: 30 	 Central_Heating:_Two_or_more_types_of_central_heating;_measures:_Value
index: 31 	 Crime:_Anti-social_behaviour
index: 32 	 Crime:_Burglary
index: 33 	 Crime:_Criminal_damage_and_arson
index: 34 	 Crime:_Drugs
index: 35 	 Crime:_Other_crime
index: 36 	 Crime:_Other_theft
index: 37 	 Crime:_Public_disorder_and_weapons
index: 38 	 Crime:_Robbery
index: 39 	 Crime:_Shoplifting
index: 40 	 Crime:_Vehicle_crime
index: 41 	 Crime:_Violent_crime
index: 42 	 Distance_travelled_to_work:_10km_to_less_than_20km;_measures:_Value
index: 43 	 Distance_travelled_to_work:_20km_to_less_than_30km;_measures:_Value
index: 44 	 Distance_travelled_to_work:_2km_to_less_than_5km;_measures:_Value
index: 45 	 Distance_travelled_to_work:_30km_to_less_than_40km;_measures:_Value
index: 46 	 Distance_travelled_to_work:_40km_to_less_than_60km;_measures:_Value
index: 47 	 Distance_travelled_to_work:_5km_to_less_than_10km;_measures:_Value
index: 48 	 Distance_travelled_to_work:_60km_and_over;_measures:_Value
index: 49 	 Distance_travelled_to_work:_Less_than_2km;_measures:_Value
index: 50 	 Distance_travelled_to_work:_Other;_measures:_Value
index: 51 	 Dwelling_Type:_Shared_dwelling;_measures:_Value
index: 52 	 Dwelling_Type:_Unshared_dwelling;_measures:_Value
index: 53 	 Establishment_Type:_All_communal_establishments;_measures:_Value
index: 54 	 Establishment_Type:_Establishment_not_stated;_measures:_Value
index: 55 	 Establishment_Type:_Medical_and_care_establishment:_Local_Authority:_Care_home_or_other_home;_measures:_Value
index: 56 	 Establishment_Type:_Medical_and_care_establishment:_Local_Authority:_Children's_home_(including_secure_units);_measures:_Value
index: 57 	 Establishment_Type:_Medical_and_care_establishment:_NHS:_General_hospital;_measures:_Value
index: 58 	 Establishment_Type:_Medical_and_care_establishment:_NHS:_Mental_health_hospital/unit_(including_secure_units);_measures:_Value
index: 59 	 Establishment_Type:_Medical_and_care_establishment:_NHS:_Other_hospital;_measures:_Value
index: 60 	 Establishment_Type:_Medical_and_care_establishment:_Other:_Care_home_with_nursing;_measures:_Value
index: 61 	 Establishment_Type:_Medical_and_care_establishment:_Other:_Care_home_without_nursing;_measures:_Value
index: 62 	 Establishment_Type:_Medical_and_care_establishment:_Other:_Children's_home_(including_secure_units);_measures:_Value
index: 63 	 Establishment_Type:_Medical_and_care_establishment:_Other;_measures:_Value
index: 64 	 Establishment_Type:_Medical_and_care_establishment:_Registered_Social_Landlord/Housing_Association;_measures:_Value
index: 65 	 Establishment_Type:_Other_establishments;_measures:_Value
index: 66 	 General_Health:_Bad_health;_measures:_Value
index: 67 	 General_Health:_Fair_health;_measures:_Value
index: 68 	 General_Health:_Good_health;_measures:_Value
index: 69 	 General_Health:_Very_bad_health;_measures:_Value
index: 70 	 General_Health:_Very_good_health;_measures:_Value
index: 71 	 Hours_Worked:_Females:_Full-time:_31_to_48_hours_worked;_measures:_Value
index: 72 	 Hours_Worked:_Females:_Full-time:_49_or_more_hours_worked;_measures:_Value
index: 73 	 Hours_Worked:_Females:_Part-time:_15_hours_or_less_worked;_measures:_Value
index: 74 	 Hours_Worked:_Females:_Part-time:_16_to_30_hours_worked;_measures:_Value
index: 75 	 Hours_Worked:_Males:_Full-time:_49_or_more_hours_worked;_measures:_Value
index: 76 	 Hours_Worked:_Males:_Part-time:_15_hours_or_less_worked;_measures:_Value
index: 77 	 Hours_Worked:_Males:_Part-time:_16_to_30_hours_worked;_measures:_Value
index: 78 	 Hours_Worked:_Males:_Total;_measures:_Value
index: 79 	 Household_Composition:_One_family_only:_Cohabiting_couple:_All_children_non-dependent;_measures:_Value
index: 80 	 Household_Composition:_One_family_only:_Cohabiting_couple:_Dependent_children;_measures:_Value
index: 81 	 Household_Composition:_One_family_only:_Cohabiting_couple:_No_children;_measures:_Value
index: 82 	 Household_Composition:_One_family_only:_Lone_parent:_All_children_non-dependent;_measures:_Value
index: 83 	 Household_Composition:_One_family_only:_Lone_parent:_Dependent_children;_measures:_Value
index: 84 	 Household_Composition:_One_person_household:_Aged_65_and_over;_measures:_Value
index: 85 	 Household_Composition:_One_person_household:_Other;_measures:_Value
index: 86 	 Occupation:_1._Managers,_directors_and_senior_officials;_Age:_Age_16_to_24;_measures:_Value
index: 87 	 Occupation:_1._Managers,_directors_and_senior_officials;_Age:_Age_25_to_34;_measures:_Value
index: 88 	 Occupation:_1._Managers,_directors_and_senior_officials;_Age:_Age_35_to_49;_measures:_Value
index: 89 	 Occupation:_1._Managers,_directors_and_senior_officials;_Age:_Age_50_to_64;_measures:_Value
index: 90 	 Occupation:_1._Managers,_directors_and_senior_officials;_Age:_Age_65_and_over;_measures:_Value
index: 91 	 Occupation:_2._Professional_occupations;_Age:_Age_16_to_24;_measures:_Value
index: 92 	 Occupation:_2._Professional_occupations;_Age:_Age_25_to_34;_measures:_Value
index: 93 	 Occupation:_2._Professional_occupations;_Age:_Age_35_to_49;_measures:_Value
index: 94 	 Occupation:_2._Professional_occupations;_Age:_Age_50_to_64;_measures:_Value
index: 95 	 Occupation:_2._Professional_occupations;_Age:_Age_65_and_over;_measures:_Value
index: 96 	 Occupation:_3._Associate_professional_and_technical_occupations;_Age:_Age_16_to_24;_measures:_Value
index: 97 	 Occupation:_3._Associate_professional_and_technical_occupations;_Age:_Age_25_to_34;_measures:_Value
index: 98 	 Occupation:_3._Associate_professional_and_technical_occupations;_Age:_Age_35_to_49;_measures:_Value
index: 99 	 Occupation:_3._Associate_professional_and_technical_occupations;_Age:_Age_50_to_64;_measures:_Value
index: 100 	 Occupation:_3._Associate_professional_and_technical_occupations;_Age:_Age_65_and_over;_measures:_Value
index: 101 	 Occupation:_4._Administrative_and_secretarial_occupations;_Age:_Age_16_to_24;_measures:_Value
index: 102 	 Occupation:_4._Administrative_and_secretarial_occupations;_Age:_Age_25_to_34;_measures:_Value
index: 103 	 Occupation:_4._Administrative_and_secretarial_occupations;_Age:_Age_35_to_49;_measures:_Value
index: 104 	 Occupation:_4._Administrative_and_secretarial_occupations;_Age:_Age_50_to_64;_measures:_Value
index: 105 	 Occupation:_4._Administrative_and_secretarial_occupations;_Age:_Age_65_and_over;_measures:_Value
index: 106 	 Occupation:_5._Skilled_trades_occupations;_Age:_Age_16_to_24;_measures:_Value
index: 107 	 Occupation:_5._Skilled_trades_occupations;_Age:_Age_25_to_34;_measures:_Value
index: 108 	 Occupation:_5._Skilled_trades_occupations;_Age:_Age_35_to_49;_measures:_Value
index: 109 	 Occupation:_5._Skilled_trades_occupations;_Age:_Age_50_to_64;_measures:_Value
index: 110 	 Occupation:_5._Skilled_trades_occupations;_Age:_Age_65_and_over;_measures:_Value
index: 111 	 Occupation:_6._Caring,_leisure_and_other_service_occupations;_Age:_Age_16_to_24;_measures:_Value
index: 112 	 Occupation:_6._Caring,_leisure_and_other_service_occupations;_Age:_Age_25_to_34;_measures:_Value
index: 113 	 Occupation:_6._Caring,_leisure_and_other_service_occupations;_Age:_Age_35_to_49;_measures:_Value
index: 114 	 Occupation:_6._Caring,_leisure_and_other_service_occupations;_Age:_Age_50_to_64;_measures:_Value
index: 115 	 Occupation:_6._Caring,_leisure_and_other_service_occupations;_Age:_Age_65_and_over;_measures:_Value
index: 116 	 Occupation:_7._Sales_and_customer_service_occupations;_Age:_Age_16_to_24;_measures:_Value
index: 117 	 Occupation:_7._Sales_and_customer_service_occupations;_Age:_Age_25_to_34;_measures:_Value
index: 118 	 Occupation:_7._Sales_and_customer_service_occupations;_Age:_Age_35_to_49;_measures:_Value
index: 119 	 Occupation:_7._Sales_and_customer_service_occupations;_Age:_Age_50_to_64;_measures:_Value
index: 120 	 Occupation:_7._Sales_and_customer_service_occupations;_Age:_Age_65_and_over;_measures:_Value
index: 121 	 Occupation:_8._Process,_plant_and_machine_operatives;_Age:_Age_16_to_24;_measures:_Value
index: 122 	 Occupation:_8._Process,_plant_and_machine_operatives;_Age:_Age_25_to_34;_measures:_Value
index: 123 	 Occupation:_8._Process,_plant_and_machine_operatives;_Age:_Age_35_to_49;_measures:_Value
index: 124 	 Occupation:_8._Process,_plant_and_machine_operatives;_Age:_Age_50_to_64;_measures:_Value
index: 125 	 Occupation:_8._Process,_plant_and_machine_operatives;_Age:_Age_65_and_over;_measures:_Value
index: 126 	 Occupation:_9._Elementary_occupations;_Age:_Age_16_to_24;_measures:_Value
index: 127 	 Occupation:_9._Elementary_occupations;_Age:_Age_25_to_34;_measures:_Value
index: 128 	 Occupation:_9._Elementary_occupations;_Age:_Age_35_to_49;_measures:_Value
index: 129 	 Occupation:_9._Elementary_occupations;_Age:_Age_50_to_64;_measures:_Value
index: 130 	 Occupation:_9._Elementary_occupations;_Age:_Age_65_and_over;_measures:_Value
index: 131 	 Passports_Held:_Africa;_measures:_Value
index: 132 	 Passports_Held:_Antarctica_and_Oceania;_measures:_Value
index: 133 	 Passports_Held:_British_Overseas_Territories;_measures:_Value
index: 134 	 Passports_Held:_Central_America;_measures:_Value
index: 135 	 Passports_Held:_Middle_East_and_Asia;_measures:_Value
index: 136 	 Passports_Held:_North_America_and_the_Caribbean;_measures:_Value
index: 137 	 Passports_Held:_Other_Europe:_EU_countries;_measures:_Value
index: 138 	 Passports_Held:_Other_Europe:_Non_EU_countries;_measures:_Value
index: 139 	 Passports_Held:_Republic_of_Ireland;_measures:_Value
index: 140 	 Passports_Held:_South_America;_measures:_Value
index: 141 	 Passports_Held:_United_Kingdom;_measures:_Value
index: 142 	 Tenure:_Living_rent_free;_measures:_Value
index: 143 	 Tenure:_Owned:_Owned_outright;_measures:_Value
index: 144 	 Tenure:_Owned:_Owned_with_a_mortgage_or_loan;_measures:_Value
index: 145 	 Tenure:_Owned;_measures:_Value
index: 146 	 Tenure:_Private_rented:_Other;_measures:_Value
index: 147 	 Tenure:_Private_rented:_Private_landlord_or_letting_agency;_measures:_Value
index: 148 	 Tenure:_Private_rented;_measures:_Value
index: 149 	 Tenure:_Shared_ownership_(part_owned_and_part_rented);_measures:_Value
index: 150 	 Tenure:_Social_rented:_Other;_measures:_Value
index: 151 	 Tenure:_Social_rented:_Rented_from_council_(Local_Authority);_measures:_Value
index: 152 	 Tenure:_Social_rented;_measures:_Value
index: 153 	 Qualification:_No_qualifications;_measures:_Value
index: 154 	 Qualification:_Level_1_qualifications;_measures:_Value
index: 155 	 Qualification:_Level_2_qualifications;_measures:_Value
index: 156 	 Qualification:_Apprenticeship;_measures:_Value
index: 157 	 Qualification:_Level_3_qualifications;_measures:_Value
index: 158 	 Qualification:_Level_4_qualifications_and_above;_measures:_Value
index: 159 	 Qualification:_Other_qualifications;_measures:_Value
index: 160 	 Method_of_Travel_to_Work:_Work_mainly_at_or_from_home;_measures:_Value
index: 161 	 Method_of_Travel_to_Work:_Underground,_metro,_light_rail,_tram;_measures:_Value
index: 162 	 Method_of_Travel_to_Work:_Train;_measures:_Value
index: 163 	 Method_of_Travel_to_Work:_Bus,_minibus_or_coach;_measures:_Value
index: 164 	 Method_of_Travel_to_Work:_Taxi;_measures:_Value
index: 165 	 Method_of_Travel_to_Work:_Motorcycle,_scooter_or_moped;_measures:_Value
index: 166 	 Method_of_Travel_to_Work:_Driving_a_car_or_van;_measures:_Value
index: 167 	 Method_of_Travel_to_Work:_Passenger_in_a_car_or_van;_measures:_Value
index: 168 	 Method_of_Travel_to_Work:_Bicycle;_measures:_Value
index: 169 	 Method_of_Travel_to_Work:_On_foot;_measures:_Value
index: 170 	 Method_of_Travel_to_Work:_Other_method_of_travel_to_work;_measures:_Value
index: 171 	 Method_of_Travel_to_Work:_Not_in_employment;_measures:_Value
index: 172 	 Occupation:_1._Managers,_directors_and_senior_officials
index: 173 	 Occupation:_2._Professional_occupations
index: 174 	 Occupation:_3._Associate_professional_and_technical_occupations
index: 175 	 Occupation:_4._Administrative_and_secretarial_occupations
index: 176 	 Occupation:_5._Skilled_trades_occupations
index: 177 	 Occupation:_6._Caring,_leisure_and_other_service_occupations
index: 178 	 Occupation:_7._Sales_and_customer_service_occupations
index: 179 	 Occupation:_8._Process,_plant_and_machine_operatives
index: 180 	 Occupation:_9._Elementary_occupations
index: 181 	 FlickrPhotoCount
index: 182 	 Mean_of_Medians_Weighted_by_Sale_Counts_(2009_to_2013)
index: 183 	 Main_Language:_All_people_aged_16_and_over_in_household_have_English_as_a_main_language_(English_or_Welsh_in_Wales);_measures:_Value
index: 184 	 Main_Language:_At_least_one_but_not_all_people_aged_16_and_over_in_household_have_English_as_a_main_language_(English_or_Welsh_in_Wales);_measures:_Value
index: 185 	 Main_Language:_No_people_aged_16_and_over_in_household_but_at_least_one_person_aged_3_to_15_has_English_as_a_main_language_(English_or_Welsh_in_Wales);_measures:_Value
index: 186 	 Main_Language:_No_people_in_household_have_English_as_a_main_language_(English_or_Welsh_in_Wales);_measures:_Value
index: 187 	 Foodagency:_per_km_squared
index: 188 	 Pubs_per_square_km
index: 189 	 NS-SeC:_1._Higher_managerial,_administrative_and_professional_occupations;_Economic_Activity:_Economically_active:_In_employment:_Employee:_Part-time_(including_full-time_students);_measures:_Value
index: 190 	 NS-SeC:_1._Higher_managerial,_administrative_and_professional_occupations;_Economic_Activity:_Economically_active:_In_employment:_Employee:_Full-time_(including_full-time_students);_measures:_Value
index: 191 	 NS-SeC:_1._Higher_managerial,_administrative_and_professional_occupations;_Economic_Activity:_Economically_active:_In_employment:_Self-employed:_Part-time_(including_full-time_students);_measures:_Value
index: 192 	 NS-SeC:_1._Higher_managerial,_administrative_and_professional_occupations;_Economic_Activity:_Economically_active:_In_employment:_Self-employed:_Full-time_(including_full-time_students);_measures:_Value
index: 193 	 NS-SeC:_1._Higher_managerial,_administrative_and_professional_occupations;_Economic_Activity:_Economically_active:_Unemployed_(including_full-time_students);_measures:_Value
index: 194 	 NS-SeC:_1._Higher_managerial,_administrative_and_professional_occupations;_Economic_Activity:_Economically_inactive;_measures:_Value
index: 195 	 NS-SeC:_2._Lower_managerial,_administrative_and_professional_occupations;_Economic_Activity:_Economically_active:_In_employment:_Employee:_Part-time_(including_full-time_students);_measures:_Value
index: 196 	 NS-SeC:_2._Lower_managerial,_administrative_and_professional_occupations;_Economic_Activity:_Economically_active:_In_employment:_Employee:_Full-time_(including_full-time_students);_measures:_Value
index: 197 	 NS-SeC:_2._Lower_managerial,_administrative_and_professional_occupations;_Economic_Activity:_Economically_active:_In_employment:_Self-employed:_Part-time_(including_full-time_students);_measures:_Value
index: 198 	 NS-SeC:_2._Lower_managerial,_administrative_and_professional_occupations;_Economic_Activity:_Economically_active:_In_employment:_Self-employed:_Full-time_(including_full-time_students);_measures:_Value
index: 199 	 NS-SeC:_2._Lower_managerial,_administrative_and_professional_occupations;_Economic_Activity:_Economically_active:_Unemployed_(including_full-time_students);_measures:_Value
index: 200 	 NS-SeC:_2._Lower_managerial,_administrative_and_professional_occupations;_Economic_Activity:_Economically_inactive;_measures:_Value
index: 201 	 NS-SeC:_3._Intermediate_occupations;_Economic_Activity:_Economically_active:_In_employment:_Employee:_Part-time_(including_full-time_students);_measures:_Value
index: 202 	 NS-SeC:_3._Intermediate_occupations;_Economic_Activity:_Economically_active:_In_employment:_Employee:_Full-time_(including_full-time_students);_measures:_Value
index: 203 	 NS-SeC:_3._Intermediate_occupations;_Economic_Activity:_Economically_active:_In_employment:_Self-employed:_Part-time_(including_full-time_students);_measures:_Value
index: 204 	 NS-SeC:_3._Intermediate_occupations;_Economic_Activity:_Economically_active:_In_employment:_Self-employed:_Full-time_(including_full-time_students);_measures:_Value
index: 205 	 NS-SeC:_3._Intermediate_occupations;_Economic_Activity:_Economically_active:_Unemployed_(including_full-time_students);_measures:_Value
index: 206 	 NS-SeC:_3._Intermediate_occupations;_Economic_Activity:_Economically_inactive;_measures:_Value
index: 207 	 NS-SeC:_4._Small_employers_and_own_account_workers;_Economic_Activity:_Economically_active:_In_employment:_Self-employed:_Part-time_(including_full-time_students);_measures:_Value
index: 208 	 NS-SeC:_4._Small_employers_and_own_account_workers;_Economic_Activity:_Economically_active:_In_employment:_Self-employed:_Full-time_(including_full-time_students);_measures:_Value
index: 209 	 NS-SeC:_4._Small_employers_and_own_account_workers;_Economic_Activity:_Economically_active:_Unemployed_(including_full-time_students);_measures:_Value
index: 210 	 NS-SeC:_4._Small_employers_and_own_account_workers;_Economic_Activity:_Economically_inactive;_measures:_Value
index: 211 	 NS-SeC:_5._Lower_supervisory_and_technical_occupations;_Economic_Activity:_Economically_active:_In_employment:_Employee:_Part-time_(including_full-time_students);_measures:_Value
index: 212 	 NS-SeC:_5._Lower_supervisory_and_technical_occupations;_Economic_Activity:_Economically_active:_In_employment:_Employee:_Full-time_(including_full-time_students);_measures:_Value
index: 213 	 NS-SeC:_5._Lower_supervisory_and_technical_occupations;_Economic_Activity:_Economically_active:_In_employment:_Self-employed:_Part-time_(including_full-time_students);_measures:_Value
index: 214 	 NS-SeC:_5._Lower_supervisory_and_technical_occupations;_Economic_Activity:_Economically_active:_In_employment:_Self-employed:_Full-time_(including_full-time_students);_measures:_Value
index: 215 	 NS-SeC:_5._Lower_supervisory_and_technical_occupations;_Economic_Activity:_Economically_active:_Unemployed_(including_full-time_students);_measures:_Value
index: 216 	 NS-SeC:_5._Lower_supervisory_and_technical_occupations;_Economic_Activity:_Economically_inactive;_measures:_Value
index: 217 	 NS-SeC:_6._Semi-routine_occupations;_Economic_Activity:_Economically_active:_In_employment:_Employee:_Part-time_(including_full-time_students);_measures:_Value
index: 218 	 NS-SeC:_6._Semi-routine_occupations;_Economic_Activity:_Economically_active:_In_employment:_Employee:_Full-time_(including_full-time_students);_measures:_Value
index: 219 	 NS-SeC:_6._Semi-routine_occupations;_Economic_Activity:_Economically_active:_In_employment:_Self-employed:_Part-time_(including_full-time_students);_measures:_Value
index: 220 	 NS-SeC:_6._Semi-routine_occupations;_Economic_Activity:_Economically_active:_In_employment:_Self-employed:_Full-time_(including_full-time_students);_measures:_Value
index: 221 	 NS-SeC:_6._Semi-routine_occupations;_Economic_Activity:_Economically_active:_Unemployed_(including_full-time_students);_measures:_Value
index: 222 	 NS-SeC:_6._Semi-routine_occupations;_Economic_Activity:_Economically_inactive;_measures:_Value
index: 223 	 NS-SeC:_7._Routine_occupations;_Economic_Activity:_Economically_active:_In_employment:_Employee:_Part-time_(including_full-time_students);_measures:_Value
index: 224 	 NS-SeC:_7._Routine_occupations;_Economic_Activity:_Economically_active:_In_employment:_Employee:_Full-time_(including_full-time_students);_measures:_Value
index: 225 	 NS-SeC:_7._Routine_occupations;_Economic_Activity:_Economically_active:_Unemployed_(including_full-time_students);_measures:_Value
index: 226 	 NS-SeC:_7._Routine_occupations;_Economic_Activity:_Economically_inactive;_measures:_Value
index: 227 	 NS-SeC:_8._Never_worked_and_long-term_unemployed;_Economic_Activity:_Economically_active:_Unemployed_(including_full-time_students);_measures:_Value
index: 228 	 NS-SeC:_8._Never_worked_and_long-term_unemployed;_Economic_Activity:_Economically_inactive;_measures:_Value
index: 229 	 NS-SeC:_L15_Full-time_students;_Economic_Activity:_Economically_active:_In_employment:_Employee:_Part-time_(including_full-time_students);_measures:_Value
index: 230 	 NS-SeC:_L15_Full-time_students;_Economic_Activity:_Economically_active:_In_employment:_Employee:_Full-time_(including_full-time_students);_measures:_Value
index: 231 	 NS-SeC:_L15_Full-time_students;_Economic_Activity:_Economically_active:_In_employment:_Self-employed:_Part-time_(including_full-time_students);_measures:_Value
index: 232 	 NS-SeC:_L15_Full-time_students;_Economic_Activity:_Economically_active:_In_employment:_Self-employed:_Full-time_(including_full-time_students);_measures:_Value
index: 233 	 NS-SeC:_L15_Full-time_students;_Economic_Activity:_Economically_active:_Unemployed_(including_full-time_students);_measures:_Value
index: 234 	 NS-SeC:_L15_Full-time_students;_Economic_Activity:_Economically_inactive;_measures:_Value
index: 235 	 Greenspace:_Local_Parks_per_square_km
****************************************************************
Data Details
Number of Attributes: 235
Running Clusterer.
Writing means output: v6.centroids.txt
Writing output: v6.clusters.csv
Finished.
Took 14.603 seconds.

```