install.packages("tm")
install.packages("topicmodels")
install.packages("SnowballC")
install.packages("ldatuning")
library(tm)
library(topicmodels)
library(SnowballC)
library(ldatuning)


setwd("F:/OneDrive/Documents/Java Web Workspace/rs-movie/backend/resource-server/src/data/reviews/txt")

filenames <- list.files(getwd(), pattern = "*.txt")
files <- lapply(filenames, readLines)
docs <- Corpus(VectorSource(files))
writeLines(as.character(docs[2]))

docs <- tm_map(docs, content_transformer(tolower))
writeLines(as.character(docs[2]))
toSpace <- content_transformer(function(x, pattern) {return (gsub(pattern, " ", x))})
docs <- tm_map(docs, toSpace, "\"")
writeLines(as.character(docs[2]))
docs <- tm_map(docs, toSpace, "/")
writeLines(as.character(docs[2]))
docs <- tm_map(docs, toSpace, "\\\\")
writeLines(as.character(docs[2]))
docs <- tm_map(docs, removePunctuation)
writeLines(as.character(docs[2]))
docs <- tm_map(docs, removeNumbers)
writeLines(as.character(docs[2]))
docs <- tm_map(docs, removeWords, stopwords("english"))
writeLines(as.character(docs[2]))
docs <- tm_map(docs, stripWhitespace)
writeLines(as.character(docs[2]))

docs <- tm_map(docs, stemDocument, "english")
writeLines(as.character(docs[2]))
docs <- tm_map(docs, content_transformer(gsub), 
               pattern = "organiz", replacement = "organ")
writeLines(as.character(docs[2]))
docs <- tm_map(docs, content_transformer(gsub), 
               pattern = "organis", replacement = "organ")
writeLines(as.character(docs[2]))
docs <- tm_map(docs, content_transformer(gsub), 
               pattern = "andgovern", replacement = "govern")
writeLines(as.character(docs[2]))
docs <- tm_map(docs, content_transformer(gsub), 
               pattern = "inenterpris", replacement = "enterpris")
writeLines(as.character(docs[2]))
docs <- tm_map(docs, content_transformer(gsub), 
               pattern = "team-", replacement = "team")
writeLines(as.character(docs[2]))

numdocs <- length(docs)
minDocFreq <- numdocs * 0.02
maxDocFreq <- numdocs * 0.1
dtm <- DocumentTermMatrix(docs, control = list(bounds = list(global = c(minDocFreq, maxDocFreq))))
rownames(dtm) <- filenames
freq <- colSums(as.matrix(dtm))
length(freq)
ord <- order(freq, decreasing = TRUE)
freq[ord]
write.csv(freq[ord], "word_freq.csv")

result <- FindTopicsNumber(dtm, topics = seq(from = 2, to = 50, by = 1),
            metrics = c("Griffiths2004", "CaoJuan2009", "Arun2010", "Deveaud2014"), 
            method = "Gibbs", control = list(seed = 77), mc.cores = 2L, verbose = TRUE)
result
FindTopicsNumber_plot(result)

burnin <- 4000
iter <- 2000
thin <- 500
seed <- list(2003, 5, 63, 100001, 765)
nstart <- 5
best <- TRUE
k <- 25

ldaOut <- LDA(dtm, k, method = "Gibbs", control = list(nstart = nstart, 
                                                       seed = seed, best = best, burnin = burnin, iter = iter, thin = thin))
ldaOut <- LDA(dtm, k, method = "VEM", control = list(estimate.alpha = TRUE, alpha = 50/k, 
            estimate.beta = TRUE, verbose = 0, prefix = tempfile(), save = 0, keep = 0, 
            seed = as.integer(Sys.time()), nstart = 1, best = best, 
            var = list(iter.max = 500, tol = 10^-6), em = list(iter.max = 1000, tol = 10^-4), 
            initialize = "random"))
ldaOut.topics <- as.matrix(topics(ldaOut))
write.csv(ldaOut.topics, file = paste("LDAVEM", k, "DocsToTopics.csv"))
ldaOut.terms <- as.matrix(terms(ldaOut, 50))
write.csv(ldaOut.terms, file = paste("LDAVEM", k, "TopicsToTerms.csv"))
topicProbabilities <- as.data.frame(ldaOut@gamma)
write.csv(topicProbabilities, file = paste("LDAVEM", k, "TopicProbabilities.csv"))

topic1ToTopic2 <- lapply(1:nrow(dtm), function(x) { 
  sort(topicProbabilities[x,])[k]/sort(topicProbabilities[x,])[k-1]})
topic2ToTopic3 <- lapply(1:nrow(dtm), function(x) { 
  sort(topicProbabilities[x,])[k-1]/sort(topicProbabilities[x,])[k-2]})
write.csv(topic1ToTopic2, file=paste("LDAVEM", k, "Topic1ToTopic2.csv"))
write.csv(topic2ToTopic3, file=paste("LDAVEM", k, "Topic2ToTopic3.csv"))