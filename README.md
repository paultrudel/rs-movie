# Movie Recommender System (RS-Movie)

The purpose of this project was to get a better understanding of how an information system can make recommendations to its users. In addition, this project served as an excellent opporunity to hone my skills with both Spring and Angular, and get some experience with user access management using Keycloak. A portion of the the MovieLens dataset was used as a basis for starting this project. The dataset includes 82,201 reviews from 1252 users on 1079 movies with the reviews scoring movies on a scale of 1 to 5.


## Predicting User Scores

One of the most important things a recommender system must do is be able to accurately predict how a user would rate a particular item. In the dataset for this project the 82,201 reviews with known ratings only encompass ~6% of all possible user ratings, and in many practical applications this data is even more sparse with <1% of user ratings being actually known. In this project four techniques were used to predict unknown user scores and their effectiveness was evaluated using the common metric of root mean squared error. 

![RMSE]

### Collaborative Filtering (CF)

Collaborative filtering works by assuming that other user's opinions about an item, in this case a movie, can be used to reasonably predict a specific user's opinion on an item. The intuition is that if a group of user's agree on the quality of some items they will then likely agree about the quality of other items. Two of the most common collaborative filtering techinques are user-to-user (user-based) and item-to-item (item-based) CF.

#### User-Based CF

To predict a user's ratings for a specific item user-based CF seeks to identify other users in the system who have similar rating behaviour and have rated the item in question. The similarity in rating behaviour between two users is most commonly measured using the Pearson correlation coefficient.

![User_Based_CF_Similarity]

To generate a rating prediction the similarity function is used to create a neighbourhood, N, of similar users. Only users who have a positive correlation coefficient are taken into account and of those users normally only the top 20 to 50 are actually placed in this neighbourhood. The prediction is computed using the weighted average of the neighbouring user's rating of the specific item.

![User_Based_CF_Prediction]

For the dataset in this project user-based collaborative filtering produced an RMSE of 0.68.

#### Item-Based CF

Item-to-item CF functions similariliy to user-to-user CF but uses the similarity between items instead of users to make predictions. User-based CF suffers from issues with scalability and the major benefit that comes with item-based CF is the ability to pre-compute the similarity matrix since it is unlikely that any one user adding or changing a rating will have a significant impact on the similarity between two items. The matrix data will slowly become out-of-date over time but it can be refreshed periodically by re-computing the item similarities. The most common measure of similarity between two items is their cosine similarity.

![Item_Based_CF_Similarity]

Like with user-based CF an neighbourhood, N, of most similar items is created. Only items with a postivie similarity score are considered and of those again only the top 20 to 50 are selected. A final prediction is generated using a weighted average.

![Item_Based_CF_Prediction]

Item-based collaborative filtering predicted scores with an RMSE of 0.57

### Singular Value Decomposition (SVD)

The user-item ratings domain can be viewed as a vector space the issue is however that the vectors are of very high dimension and contain redundancies. Therefore, it would be desirable to reduce the dimensionality of the rating space. One of the most popular methods of doing this is with singular value decomposition (SVD). A matrix, M, can be factorized into three matrices U, Σ, and V. Matrix Σ is a diagonal matrix whose entries are the singular values of the decomposition, U and V are orthogonal matrices.

![SVD]

Performing this decomposition accomplishes little initially. However, the dimensionality of these matrices can be easily reduced. The matrix Σ can be truncated by only retaining the k largest singluar values, where k is the number of singular values which retain 80%-90% of the "energy", or total value, in the matrix Σ.

![SVD_Approximation]

SVD is only well-defined when a matrix is complete and therefore it should not be directly computed on the sparse ratings matrix. There are multiple solutions to this problem two of which were implemented in this project; imputation and gradient descent.

#### SVD Using Imputation

With imputation the missing values in the ratings matrix are filled with a reasonable default value. Generally this default value is either the item's or user's average rating. For this project missing values were imputed using the movie's average rating. Predictions are made by computing the weighted dot product of the user-factor vector and the item-factor vector.

![SVD_Prediction]

SVD with imputation predicted scores with an RMSE of 0.94

#### SVD Using Gradient Descent

Instead of imputing missing values it is possible to compute SVD on the sparse ratings matrix by treating missing values as 0. Gradient descent can then be used to adjust the factor matrices before using them for prediction.

![SVD_Gradient_Descent]


## Topic Analysis


## Clustering Users


[RMSE]: https://latex.codecogs.com/gif.latex?RMSE%3D%5Csqrt%7B%5Cfrac%7B%5Csum_%7B%28u%2Cm%29%5Cin%7BR%7D%7D%28r_%7Bu%2Cm%7D-%5Chat%7Br%7D_%7Bu%2Cm%7D%29%5E2%7D%7B%7CR%7C%7D%7D%20%5C%5C%5C%5C%20%5Cindent%20R%20%3D%20set%5C%3Aof%5C%3Aactual%5C%3Aratings%20%5C%5C%5C%5C%20%5Cindent%20r_%7Bu%2Cm%7D%20%3D%20actual%5C%3Arating%5C%3Aof%5C%3Amovie%5C%3Am%5C%3Aby%5C%3Auser%5C%3Au%20%5C%5C%5C%5C%20%5Cindent%20%5Chat%7Br%7D_%7Bu%2Cm%7D%3D%20predicted%5C%3Arating%5C%3Aof%5C%3Amovie%5C%3Am%5C%3Aby%5C%3Auser%5C%3Au

[User_Based_CF_Similarity]: https://latex.codecogs.com/gif.latex?sim%28u%2Cv%29%3D%5Cfrac%7B%5Csum_%7Bi%20%5Cin%20I%7D%28r_%7Bu%2Ci%7D-%5Cbar%7Br%7D_u%29%28r_%7Bv%2Ci%7D-%5Cbar%7Br%7D_v%29%7D%7B%5Csqrt%7B%5Csum_%7Bi%20%5Cin%20I%7D%28r_%7Bu%2Ci%7D-%5Cbar%7Br%7D_u%29%5E2%7D%5Csqrt%7B%5Csum_%7Bi%20%5Cin%20I%7D%28r_%7Bv%2Ci%7D-%5Cbar%7Br%7D_v%29%5E2%7D%7D%20%5C%5C%5C%5C%20%5Cindent%20I%3DI_u%20%5Ccap%20I_v%3Dset%5C%3Aof%5C%3Aall%5C%3Aitems%5C%3Arated%5C%3Aby%5C%3Aboth%5C%3Ausers%20%5C%5C%5C%5C%20%5Cindent%20r_%7Bu%2Ci%7D%3Drating%5C%3Aof%5C%3Aitem%5C%3Ai%5C%3Aby%5C%3Auser%5C%3Au%20%5C%5C%5C%5C%20%5Cindent%20%5Cbar%7Br%7D_u%3Daverage%5C%3Arating%5C%3Agiven%5C%3Aby%5C%3Auser%5C%3Au%20%5C%5C%5C%5C%20%5Cindent%20r_%7Bv%2Ci%7D%3Drating%5C%3Aof%5C%3Aitem%5C%3Ai%5C%3Aby%5C%3Auser%5C%3Av%20%5C%5C%5C%5C%20%5Cindent%20%5Cbar%7Br%7D_v%3Daverage%5C%3Arating%5C%3Agiven%5C%3Aby%5C%3Auser%5C%3Av

[User_Based_CF_Prediction]: https://latex.codecogs.com/gif.latex?p_%7Bu%2Ci%7D%3D%5Cbar%7Br%7D_u&plus;%5Cfrac%7B%5Csum_%7Bu%5E%5Cprime%20%5Cin%20N%7Dsim%28u%2Cu%5E%5Cprime%29%28r_%7Bu%5E%5Cprime%2Ci%7D-%5Cbar%7Br%7D_%7Bu%5E%5Cprime%7D%29%7D%7B%5Csum_%7Bu%5E%5Cprime%20%5Cin%20N%7Dsim%28u%2Cu%5E%5Cprime%29%7D%20%5C%5C%5C%5C%20%5Cindent%20p_%7Bu%2Ci%7D%3Dpredicted%5C%3Arating%5C%3Aof%5C%3Aitem%5C%3Ai%5C%3Aby%5C%3Auser%5C%3Au%20%5C%5C%5C%5C%20%5Cindent%20N%3Dneighbourhood%5C%3Aof%5C%3Asimilar%5C%3Ausers%20%5C%5C%5C%5C%20%5Cindent%20%5Cbar%7Br%7D_u%3Daverage%5C%3Arating%5C%3Agiven%5C%3Aby%5C%3Auser%5C%3Au%20%5C%5C%5C%5C%20%5Cindent%20sim%28u%2Cu%5E%5Cprime%29%3Dsimilarity%5C%3Abetween%5C%3Ausers%5C%3Au%5C%3Aand%5C%3Au%5E%5Cprime%20%5C%5C%5C%5C%20%5Cindent%20r_%7Bu%5E%5Cprime%2Ci%7D%3Drating%5C%3Aof%5C%3Aitem%5C%3Ai%5C%3Aby%5C%3Auser%5C%3Au%5E%5Cprime%20%5C%5C%5C%5C%20%5Cindent%20%5Cbar%7Br%7D_%7Bu%5E%5Cprime%7D%3Daverage%5C%3Arating%5C%3Agiven%5C%3Aby%5C%3Auser%5C%3Au%5E%5Cprime

[Item_Based_CF_Similarity]: https://latex.codecogs.com/gif.latex?sim%28%5Cvec%7Ba%7D%2C%5Cvec%7Bb%7D%29%20%3D%20%5Cfrac%7B%5Cvec%7Ba%7D%5Cbullet%5Cvec%7Bb%7D%7D%7B%7C%7C%5Cvec%7Ba%7D%7C%7C%5Ccdot%7C%7C%5Cvec%7Bb%7D%7C%7C%7D%20%5C%5C%5C%5C%20%5Cindent%20%5Cvec%7Ba%7D%3Drating%5C%3Avector%5C%3Afor%5C%3Aitem%5C%3Aa%20%5C%5C%5C%5C%20%5Cindent%20%5Cvec%7Bb%7D%3Drating%5C%3Avector%5C%3Afor%5C%3Aitem%5C%3Ab%20%5C%5C%5C%5C%5C%5C%20%5Cindent%20Adjusted%5C%3Acosine%5C%3Asimilarity%20%5C%5C%5C%5C%20%5Cindent%20sim%28%5Cvec%7Ba%7D%2C%5Cvec%7Bb%7D%29%20%3D%20%5Cfrac%7B%5Csum_%7Bu%20%5Cin%20U%7D%28r_%7Bu%2Ca%7D-%5Cbar%7Br%7D_u%29%28r_%7Bu%2Cb%7D-%5Cbar%7Br%7D_u%29%7D%7B%5Csqrt%7B%5Csum_%7Bu%20%5Cin%20U%7D%28r_%7Bu%2Ca%7D-%5Cbar%7Br%7D_u%29%5E2%7D%5Csqrt%7B%5Csum_%7Bu%20%5Cin%20U%7D%28r_%7Bu%2Cb%7D-%5Cbar%7Br%7D_u%29%5E2%7D%7D%20%5C%5C%5C%5C%20%5Cindent%20U%3Dset%5C%3Aof%5C%3Ausers%5C%3Awho%5C%3Ahave%5C%3Arated%5C%3Aboth%5C%3Aitems%20%5C%5C%5C%5C%20%5Cindent%20r_%7Bu%2Ca%7D%3Drating%5C%3Aof%5C%3Aitem%5C%3Aa%5C%3Aby%5C%3Auser%5C%3Au%20%5C%5C%5C%5C%20%5Cindent%20r_%7Bu%2Cb%7D%3Drating%5C%3Aof%5C%3Aitem%5C%3Ab%5C%3Aby%5C%3Auser%5C%3Au%20%5C%5C%5C%5C%20%5Cindent%20%5Cbar%7Br%7D_u%3Daverage%5C%3Arating%5C%3Agiven%5C%3Aby%5C%3Auser%5C%3Au

[Item_Based_CF_Prediction]: https://latex.codecogs.com/gif.latex?p_%7Bu%2Ci%7D%20%3D%20%5Cfrac%7B%5Csum_%7Bi%5E%5Cprime%20%5Cin%20N%7Dsim%28i%2Ci%5E%5Cprime%29r_%7Bu%2Ci%5E%5Cprime%7D%7D%7B%5Csum_%7Bi%5E%5Cprime%20%5Cin%20N%7Dsim%28i%2Ci%5E%5Cprime%29%7D%20%5C%5C%5C%5C%20%5Cindent%20p_%7Bu%2Ci%7D%3Dpredicted%5C%3Arating%5C%3Aof%5C%3Aitem%5C%3Ai%5C%3Aby%5C%3Auser%5C%3Au%5C%5C%5C%5C%20%5Cindent%20N%3Dnegihbourhood%5C%3Aof%5C%3Asimilar%5C%3Aitems%20%5C%5C%5C%5C%20%5Cindent%20sim%28i%2Ci%5E%5Cprime%29%3Dsimilarity%5C%3Abetween%5C%3Aitems%5C%3Ai%5C%3Aand%5C%3Ai%5E%5Cprime%20%5C%5C%5C%5C%20%5Cindent%20r_%7Bu%2Ci%5E%5Cprime%7D%3Drating%5C%3Aof%5C%3Aitem%5C%3Ai%5E%5Cprime%5C%3Aby%5C%3Auser%5C%3Au

[SVD]: https://latex.codecogs.com/gif.latex?Singular%5C%3Avalue%5C%3Adecomposition%5C%3Aof%5C%3Amatrix%5C%3AR%20%5C%5C%5C%5C%20%5Cindent%20M%3DU%20%5CSigma%20V%5ET%20%5C%5C%20%5Cindent%20M%20%5Cin%20%5Cmathbb%7BR%7D%5E%7Bu%20%5Ctimes%20m%7D%20%5C%5C%20%5Cindent%20U%20%5Cin%20%5Cmathbb%7BR%7D%5E%7Bu%20%5Ctimes%20u%7D%20%5C%5C%20%5Cindent%20%5CSigma%20%5Cin%20%5Cmathbb%7BR%7D%5E%7Bu%20%5Ctimes%20m%7D%20%5C%5C%20%5Cindent%20V%5ET%20%5Cin%20%5Cmathbb%7BR%7D%5E%7Bm%20%5Ctimes%20m%7D%20%5C%5C%5C%5C%20%5Cindent%20u%3Dnumber%5C%3Aof%5C%3Ausers%20%5C%5C%20%5Cindent%20m%3Dnumber%5C%3Aof%5C%3Amovies

[SVD_Approximation]: https://latex.codecogs.com/gif.latex?Rank%5C%21-%5C%21k%5C%3Aapproximation%5C%3Aof%5C%3Amatrix%5C%3AM%20%5C%5C%5C%5C%20%5Cindent%20%5Chat%7BM%7D%3DU%20%5CSigma%20V%5ET%20%5C%5C%20%5Cindent%20%5Chat%7BM%7D%20%5Cin%20%5Cmathbb%7BR%7D%5E%7Bu%20%5Ctimes%20m%7D%20%5C%5C%20%5Cindent%20U%20%5Cin%20%5Cmathbb%7BR%7D%5E%7Bu%20%5Ctimes%20k%7D%20%5C%5C%20%5Cindent%20%5CSigma%20%5Cin%20%5Cmathbb%7BR%7D%5E%7Bk%20%5Ctimes%20k%7D%20%5C%5C%20%5Cindent%20V%5ET%20%5Cin%20%5Cmathbb%7BR%7D%5E%7Bk%20%5Ctimes%20m%7D%20%5C%5C

[SVD_Prediction]: https://latex.codecogs.com/gif.latex?p_%7Bu%2Ci%7D%3DU_uI_i%20%5C%5C%5C%5C%20%5Cindent%20M%3DU%20%5CSigma%20V%5ET%20%5C%5C%20%5Cindent%20U%20%3D%20U%20%5C%5C%20%5Cindent%20I%20%3D%20%5CSigma%20V%5ET%5C%5C%5C%5C%20%5Cindent%20U_u%3Du%5E%7Bth%7D%5C%3Arow%5C%3Aof%5C%3Amatrix%5C%3AU%20%5C%5C%20%5Cindent%20I_i%3Di%5E%7Bth%7D%5C%3Acolumn%5C%3Aof%5C%3Amatrix%5C%3AI

[SVD_Gradient_Descent]: https://latex.codecogs.com/gif.latex?M%3DU%20%5CSigma%20V%5ET%20%5C%5C%20%5Cindent%20U%3DU%20%5C%5C%20%5Cindent%20I%3D%5CSigma%20V%5ET%20%5C%5C%5C%5C%20%5Cindent%20Find%5C%3Amatrices%5C%3AU%5C%3Aand%5C%3AV%5C%3Awhich%5C%3Aminimize%20%5C%5C%20%5Cindent%20f%28x%29%3D%5Csum_%7B%7Bu%2Ci%7D%20%5Cin%20R%7D%28r_%7Bu%2Ci%7D-U_uI_i%29%5E2&plus;%5Clambda%28%5Csum_%7Bu%20%5Cin%20U%7D%7C%7CU_u%7C%7C%5E2&plus;%5Csum_%7Bi%20%5Cin%20I%7D%7C%7CI_i%7C%7C%5E2%29%20%5C%5C%5C%5C%20%5Cindent%20%5Cfrac%7B%5Cpartial%20f%7D%7B%5Cpartial%20U%7D%3D-2%5Csum_%7B%7Bu%2Ci%7D%20%5Cin%20R%7D%28r_%7Bu%2Ci%7D-U_uI_i%29I_i&plus;2%20%5Cgamma%20%7C%7CU_u%7C%7C%20%5C%5C%20%5Cindent%20%5Cfrac%7B%5Cpartial%20f%7D%7B%5Cpartial%20I%7D%3D-2%5Csum_%7B%7Bu%2Ci%7D%20%5Cin%20R%7D%28r_%7Bu%2Ci%7D-U_uI_i%29U_u&plus;2%20%5Cgamma%20%7C%7CI_i%7C%7C%20%5C%5C%5C%5C%20%5Cindent%20%5Cnabla%20U%3D%5B%5Cnabla%20U_%7Buk%7D%5D%3D-2%5Csum_%7B%7Bu%2Ci%7D%20%5Cin%20R%7D%28r_%7Bu%2Ci%7D-U_uI_i%29I_%7Bik%7D&plus;2%20%5Cgamma%20U_%7Buk%7D%20%5C%5C%20%5Cindent%20%5Cnabla%20I%3D%5B%5Cnabla%20I_%7Bki%7D%5D%3D-2%5Csum_%7B%7Bu%2Ci%7D%20%5Cin%20R%7D%28r_%7Bu%2Ci%7D-U_uI_i%29U_%7Buk%7D&plus;2%20%5Cgamma%20I_%7Bik%7D%20%5C%5C%5C%5C%20%5Cindent%20Update%5C%3Amatrices%5C%3Aby%5C%5C%20%5Cindent%20U%3DU-%5Clambda%28%5Cnabla%20U%29%20%5C%5C%20%5Cindent%20I%3DI-%5Clambda%28%5Cnabla%20I%29%20%5C%5C%5C%5C%20%5Cindent%20R%3Dset%5C%3Aof%5C%3Aactual%5C%3Aratings%20%5C%5C%20%5Cindent%20r_%7Bu%2Ci%7D%3Drating%5C%3Aof%5C%3Aitem%5C%3Ai%5C%3Aby%5C%3Auser%5C%3Au%20%5C%5C%20%5Cindent%20U_u%3Du%5E%7Bth%7D%5C%3Arow%5C%3Aof%5C%3Amatrix%5C%3AU%20%5C%5C%20%5Cindent%20I_i%3Di%5E%7Bth%7D%5C%3Acolumn%5C%3Aof%5C%3Amatrix%5C%3AI%20%5C%5C%20%5Cindent%20%5Clambda%3Dlearning%5C%3Arate%5C%3A%28typically%5C%3A0.001%29%5C%5C%20%5Cindent%20%5Cgamma%3Dregularization%5C%3Afactor%5C%3A%28typically%5C%3A0.1%29
