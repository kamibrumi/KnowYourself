library("rpart")
deteB.df = read.table("sampleForModel.txt", header = FALSE)
deteB.df
names(deteB.df) <- c("weekDay", "gb", "location", "averageTemp", "stdDevTemp", "averagePressure", "stdDevPressure", "averageHumidity", "stdDevHumidity", "averageClouds", "stdDevClouds", "averageWind", "stdDevWind", "stripId", "durationInStrips")


#II ZICEM LA R PART CA ULTIMA COLUMNA NU II PENTRU TRAINING
#deteB.df <- dete.df[,c("weekDay", "gb", "nightAverageTemp", "nightStdDevTemp", "nightAveragePressure", "nightStdDevPressure", "nightAverageHumidity", "nightStdDevHumidity", "nightAverageTempClouds", "nightStdDevClouds", "nightAverageWind", "nightStdDevWind", "dayAverageTemp", "dayStdDevTemp", "dayAveragePressure", "dayStdDevPressure", "dayAverageHumidity", "dayStdDevHumidity", "dayAverageTempClouds", "dayStdDevClouds", "dayAverageWind", "dayStdDevWind")]
#cream modelul
deteB.df$weekDay = as.factor(deteB.df$weekDay) #sa ma informez de as.factor, SA VAD DE CE NU-L DETECTEAZA CA SI FACTOR (RPARTUL)
levels(deteB.df$weekDay) <- c("1", "2", "3", "4", "5", "6", "7")
deteB.df$location = as.factor(deteB.df$location)
deteB.rpart1 = rpart(gb ~ ., data = as.data.frame(deteB.df), parms=list(split='gini'), control=rpart.control(cp=0.00001, xval=10, maxdepth=15))
plot(deteB.rpart1, uniform = TRUE) 
text(deteB.rpart1, use.n = TRUE, cex = 0.75)
#plotcp(dete.rpart1)
#printcp(dete.rpart1)
#dete.rpart1
# automatically select the complexity parameter associated with the smallest cross-validated error.
#http://www.statmethods.net/advstats/cart.html
cpPrune <- deteB.rpart1$cptable[which.min(deteB.rpart1$cptable[,"xerror"]),"CP"] 
#ACUM AR TREBUI SA PUN CP-UL ASTA AICI?
deteB.rpart2 = prune(deteB.rpart1, cp = cpPrune)
#summary(dete.rpart2)
#dete.rpart2
#plotcp(dete.rpart2)
#printcp(dete.rpart2)
plot(deteB.rpart2, uniform = TRUE)
text(deteB.rpart2, use.n = TRUE, cex = 0.75)

#predict using decision trees

#now we read the x's from the sampleForPrediction.txt file in order to conduct predictions
deteC.df = read.table("sampleForPrediction.txt", header = FALSE)
names(deteC.df) <- c("weekDay", "location", "averageTemp", "stdDevTemp", "averagePressure", "stdDevPressure", "averageHumidity", "stdDevHumidity", "averageClouds", "stdDevClouds", "averageWind", "stdDevWind", "stripId", "durationInStrips")

deteC.df$weekDay = as.factor(deteC.df$weekDay)
deteC.df$location = as.factor(deteC.df$location)

result <- predict(deteB.rpart2, deteC.df, type = "matrix", na.action = na.pass) #we are predicting for multiple individuals
result
cat("Your day will be good with a percentage of ", result) #result[1, 2]*100

#uitate pe pagina asta web:
#https://stat.ethz.ch/R-manual/R-devel/library/rpart/html/predict.rpart.html
