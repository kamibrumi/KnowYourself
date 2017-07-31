library("rpart")
dete.df = read.table("sample.txt", header = FALSE)
names(dete.df) <- c("weekDay", "gb", "nightAverageTemp", "nightStdDev", "dayAverageTemp", "dayStdDev", "nightTomorrowAverageTemp", "nightTomorrowStdDev", "dayTomorrowAverageTemp", "dayTomorrowStdDev")
#dete.df
#II ZICEM LA R PART CA ULTIMA COLUMNA NU II PENTRU TRAINING
deteB.df <- dete.df[,c("weekDay", "gb", "nightAverageTemp", "nightStdDev", "dayAverageTemp", "dayStdDev")]
#cream modelul
dete.df$weekDay = as.factor(dete.df$weekDay) #sa ma informez de as.factor, SA VAD DE CE NU-L DETECTEAZA CA SI FACTOR (RPARTUL)
dete.df$gb = as.factor(dete.df$gb)
dete.rpart1 = rpart(gb ~ ., data = as.data.frame(deteB.df), parms=list(split='gini'), control=rpart.control(cp=0.00001, xval=10, maxdepth=15))
plot(dete.rpart1, uniform = TRUE) 
text(dete.rpart1, use.n = TRUE, cex = 0.75)
#plotcp(dete.rpart1)
#printcp(dete.rpart1)
#dete.rpart1
# automatically select the complexity parameter associated with the smallest cross-validated error.
#http://www.statmethods.net/advstats/cart.html
cpPrune <- dete.rpart1$cptable[which.min(dete.rpart1$cptable[,"xerror"]),"CP"] 
#ACUM AR TREBUI SA PUN CP-UL ASTA AICI?
dete.rpart2 = prune(dete.rpart1, cp = cpPrune)
summary(dete.rpart2)
dete.rpart2
#plotcp(dete.rpart2)
#printcp(dete.rpart2)
#plot(dete.rpart2, uniform = TRUE)
#text(dete.rpart2, use.n = TRUE, cex = 0.75)

#predict using decision trees

deteC.df <- dete.df[nrow(dete.df),c("weekDay", "nightTomorrowAverageTemp", "nightTomorrowStdDev", "dayTomorrowAverageTemp", "dayTomorrowStdDev")]
deteC.df

if (deteC.df$weekDay == 7) {
  deteC.df$weekDay = as.factor(1)
} else {
  deteC.df$weekDay = as.factor(as.numeric(tail(deteC.df, 1)$"weekDay") + 1)
}

names(deteC.df) <- c("weekDay", "nightAverageTemp", "nightStdDev", "dayAverageTemp", "dayStdDev")
deteC.df$weekDay = as.numeric(deteC.df$weekDay)
result <- predict(dete.rpart2, deteC.df, type = "prob", na.action = na.pass)
print("Your day will be: ")
print(result*100)
cat("Your day will be (1. Bad 2. Good):", result*100)
#uitate pe pagina asta web:
#https://stat.ethz.ch/R-manual/R-devel/library/rpart/html/predict.rpart.html




