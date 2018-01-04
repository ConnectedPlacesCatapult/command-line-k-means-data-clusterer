# GLA Whereabouts map preview generation tool.
# Author: Rudi Ball
# 
#
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
# display.brewer.all()
ggplot() + geom_map(data = clusterdata, aes(map_id = Geography.Code, fill = Cluster), 
                    colour = "black", size=borderwidth, map = np_dist) + scale_fill_gradientn(colours = brewer.pal( numclusters , "Spectral" )) + expand_limits(x = np_dist$long, y = np_dist$lat) + theme(line = element_blank(), text = element_blank(), title = element_blank(), rect = element_blank())
#ggplot() + geom_map(data = clusterdata, aes(map_id = Geography.Code, fill = Cluster), 
#                    colour = "black", size=borderwidth, map = np_dist) + scale_fill_gradientn(colours = brewer.pal( numclusters , "Spectral" )) + expand_limits(x = np_dist$long, y = np_dist$lat)
# Close output
dev.off()



