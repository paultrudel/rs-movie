install.packages("rggobi")
install.packages("tidyverse")
install.packages("cluster")
install.packages("factoextra")
library(rggobi)
library(tidyverse)
library(cluster)
library(factoextra)

setwd("F:/OneDrive/Documents/Java Web Workspace/rs-movie/backend/resource-server/src/data/topics/")
source("F:/OneDrive/Documents/Java Web Workspace/rs-movie/backend/resource-server/src/R/functions.r")

topic_ratings <- read.csv("user-topic-scores.csv", row.names = 1, header = TRUE)
topic_ratings
fviz_nbclust(topic_ratings, kmeans, method = "wss")
fviz_nbclust(topic_ratings, kmeans, method = "silhouette")
knn <- kmeans(topic_ratings, centers = 3, nstart = 25)
fviz_cluster(knn, data = topic_ratings)
knn$cluster
write.csv(knn$cluster, file = paste("KMeans", 3, "Clusters.csv"))
summary(topic_ratings)
