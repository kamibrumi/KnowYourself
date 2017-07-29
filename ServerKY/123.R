dete.df = read.table("data1.txt", header = FALSE)
names(dete.df) <- c("weekDay", "gb")
dete.df
xtabs( ~ class, data = as.matrix(dete.df))
dete.rpart1 = rpart(gb ~ ., data = as.data.frame(dete.df), parms=list(split='gini'), control=rpart.control(cp=0.00001, xval=10, maxdepth=15))
plot(dete.rpart1, uniform = TRUE)
text(dete.rpart1, use.n = TRUE, cex = 0.75)
suplotcp(dete.rpart1)
printcp(dete.rpart1)
dete.rpart2 = prune(dete.rpart1, cp = 0.001)
plotcp(dete.rpart2)
printcp(dete.rpart2)
plot(dete.rpart2, uniform = TRUE)
text(dete.rpart2, use.n = TRUE, cex = 0.75)

dete.df$weekDay = factor(dete.df$weekDay)
dete.df$gb = factor(dete.df$gb)

dete.tree1 = tree(gb ~ weekDay.f + todayT, data = dete.df)
summary(dete.tree1)
plot(dete.tree1, uniform = TRUE)
text(dete.tree1, use.n = TRUE, cex = 0.75)
levels("weekDay") <- c("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
dete.tree1

dete.df$weekDay <- factor(dete.df$weekDay)
is.factor(dete.df$weekDay.f)
dete.rpart1 = rpart(gb ~ ., data = as.data.frame(dete.df), parms=list(split='rescaled Gini'), control=rpart.control(cp=0.00001, xval=10, maxdepth=15))

dete.rpart1 = rpart(gb ~ ., data = as.data.frame(dete.df),method = "class")
plot(dete.rpart1, uniform = TRUE)
text(dete.rpart1, use.n = TRUE, cex = 0.75)
suplotcp(dete.rpart1)
printcp(dete.rpart1)
