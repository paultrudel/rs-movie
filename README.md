# Movie Recommender System (RS-Movie)

The purpose of this project was to get a better understanding of how an information system can make recommendations to its users. In addition, this project served as an excellent opporunity to hone my skills with both Spring and Angular, and get some experience with user access management using Keycloak. A portion of the the MovieLens dataset was used as a basis for starting this project. The dataset includes 82,201 reviews from 1252 users on 1079 movies.


## Predicting User Scores

One of the most important things a recommender system must do is be able to accurately predict how a user would rate a particular item. In the dataset for this project the 82,201 reviews with known ratings only encompass ~6% of all possible user ratings, and in many practical applications this data is even more sparse with <1% of user ratings being actually known. In this project four techniques were used to predict unknown user scores and their effectiveness was evaluated using the common metric of root mean squared error. 

![RMSE]

### Collaborative Filtering (CF)

#### User-Based CF

#### Item-Based CF

### Singular Value Decomposition (SVD)

#### SVD Using Imputation

#### SVD Using Gradient Descent


## Topic Analysis


## Clustering Users


[RMSE]: https://latex.codecogs.com/gif.latex?RMSE%3D%5Csqrt%7B%5Cfrac%7B%5Csum_%7B%28u%2Cm%29%5Cin%7BR%7D%7D%28r_%7Bu%2Cm%7D-%5Chat%7Br%7D_%7Bu%2Cm%7D%29%5E2%7D%7B%7CR%7C%7D%7D%20%5C%5C%20%5Cindent%20R%20%3D%20set%5C%3Aof%5C%3Aactual%5C%3Aratings%20%5C%5C%20%5Cindent%20r_%7Bu%2Cm%7D%20%3D%20actual%5C%3Arating%5C%3Aof%5C%3Amovie%5C%3Am%5C%3Aby%5C%3Auser%5C%3Au%20%5C%5C%20%5Cindent%20%5Chat%7Br%7D_%7Bu%2Cm%7D%3D%20predicted%5C%3Arating%5C%3Aof%5C%3Amovie%5C%3Am%5C%3Aby%5C%3Auser%5C%3Au
