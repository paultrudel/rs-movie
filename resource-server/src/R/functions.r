panel.smooth.asp <- function (x, y, col = par("col"), bg = NA, pch = par("pch"), 
                              cex = 1, col.smooth = "red", span = 2/3, iter = 3, asp,...) 
{
  #browser()
  points(x, y, pch = pch, col = col, bg = bg, cex = cex, asp=1)
  ok <- is.finite(x) & is.finite(y)
  if (any(ok)) 
    lines(lowess(x[ok], y[ok], f = span, iter = iter), col = col.smooth,...) 
}

## put (absolute) correlations on the upper panels,
## with size proportional to the correlations.

panel.cor <- function(x, y, digits=2, prefix="", cex.cor) {
  usr <- par("usr"); on.exit(par(usr))
  par(usr = c(0, 1, 0, 1))
  r <- abs(cor(x, y))
  txt <- format(c(r, 0.123456789), digits=digits)[1]
  txt <- paste(prefix, txt, sep="")
  if(missing(cex.cor)) cex.cor <- 0.8/strwidth(txt)*r
  text(0.5, 0.5, txt, cex = cex.cor)
}

## put histograms on the diagonal
panel.hist <- function(x, ...) {
  usr <- par("usr"); on.exit(par(usr))
  par(usr = c(usr[1:2], 0, 1.5) )
  h <- hist(x, plot = FALSE)
  breaks <- h$breaks; nB <- length(breaks)
  y <- h$counts; y <- y/max(y)
  rect(breaks[-nB], 0, breaks[-1], y, col="cyan", ...)
}

move.centroids <- function (cb, Data, pch = "+", Dim=2) {
  ##  Repeat this step until little change in cb pos
  shift <- 100
  j <- 2
  while (shift > .003) {
    n.cb <- dim(cb)[1]
    # min.index gives the number of the nearest codebook vector to the data points
    Dist <- Array2ArrayDist(cb, Data)
    min.dist  <- apply(Dist, 2, min)
    if (n.cb == 1) {
      min.index <- apply(Dist, 2, order)
    } else {
      min.index <- apply(Dist, 2, order)[1,]
    }
    j <- j + 1
    if (Dim==2) {
      Voronoi(cb, 1:n.cb, c(200,200), range(Data[,1]), range(Data[,2]))
      points(Data, col=Col[min.index], pch=pch)
      points(cb, col="red", pch=16, cex=1.5)
    } else if (Dim==3) {
      plot3d(Data, size=3, col=Col[min.index])
      spheres3d(cb, col="red", size=9)
    }
    #  Move the codebook vectors to the middle of the group
    mid.point <- matrix(0, n.cb, Dim)
    for (c in 1:n.cb) {
      mid.point[c,] <- apply(Data[(min.index==c),], 2, mean)
    }
    if (Dim==2) {
      points(mid.point, col="green", pch=17, cex=1.5)
    } else if (Dim==3) {
      spheres3d(mid.point, col="green",size=9)
      #         s3d$points3d(mid.point, col="green", pch=17, cex=1.5)
    }
    shift <- max(mid.point - cb)                       # Could compare distortion
    cb <- mid.point
  }
  return(list(cb, min.index))
}

compute.distortion <- function(cb, Data, belongs.to) {
  N <- dim(cb)[1]
  D <- rep(0, N)
  for (i in 1:N) {
    M <- sum(belongs.to==i)
    D[i] <- sum(apply((t(t(Data[belongs.to==i,])-cb[i,]))^2, 1, sum))/M
  }
  D
}

get.pairs <- function (numb) {
  # All unique pairs
  n.comb.2 <- choose(numb,2)
  double <- matrix(0, n.comb.2, 2)
  ind <- 1
  for (i in 1:numb) {
    j <- i+1
    while (j <= numb) {  # for can run 6:5 ...
      double[ind,] <- c(i,j)
      ind <- ind + 1
      j <- j + 1
    }
  }
  double
}

Davies.Bouldin <- function(A, SS, m) {
  # A  - the centres of the clusters
  # SS - the within sum of squares
  # m  - the sizes of the clusters
  N <- nrow(A)   # number of clusters
  # intercluster distance
  S <- sqrt(SS/m)
  # Get the distances between centres
  M <- as.matrix(dist(A))
  # Get the ratio of intercluster/centre.dist
  R <- matrix(0, N, N)
  for (i in 1:(N-1)) {
    for (j in (i+1):N) {
      R[i,j] <- (S[i] + S[j])/M[i,j]
      R[j,i] <- R[i,j]
    }
  }
  return(mean(apply(R, 1, max)))
}

cluster <- function(data, num_centres) {
  base.colors <- c("#FF0000","#00FF00","#0000FF","#FF00FF","#00FFFF","#FFFF00",
                   "#800000","#008000","#000080","#FF0080","#408080","#804000",
                   "#004080", "#FF8000")
  colors <- rep(base.colors, ceiling(num_centres/length(base.colors)))
  oldpar <- par(mfrow = c(4,4))
  par(mar = c(2,1,2,1))
  errs <- rep(0, 10)
  DBI <- rep(0, 10)
  for (i in 2:15) {
    KM <- kmeans(data, i, 15)
    plot(ratings, col = colors[KM$cluster], pch = KM$cluster, main = paste(i, " clusters"))
    errs[i-1] <- sum(KM$withinss)
    DBI[i-1] <- Davies.Bouldin(KM$centers, KM$withinss, KM$size)
  }
  plot(2:15, errs, main = "SS")
  lines(2:15, errs)
  plot(2:15, DBI, main = "Davies-Bouldin")
  lines(2:15, DBI)
  par(oldpar)
}