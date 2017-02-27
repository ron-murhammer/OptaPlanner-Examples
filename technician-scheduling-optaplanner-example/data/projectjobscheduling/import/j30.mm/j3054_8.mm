************************************************************************
file with basedata            : mf54_.bas
initial value random generator: 1588665339
************************************************************************
projects                      :  1
jobs (incl. supersource/sink ):  32
horizon                       :  263
RESOURCES
  - renewable                 :  2   R
  - nonrenewable              :  2   N
  - doubly constrained        :  0   D
************************************************************************
PROJECT INFORMATION:
pronr.  #jobs rel.date duedate tardcost  MPM-Time
    1     30      0       31       10       31
************************************************************************
PRECEDENCE RELATIONS:
jobnr.    #modes  #successors   successors
   1        1          3           2   3   4
   2        3          3           5  11  17
   3        3          3           6   9  12
   4        3          3           8   9  24
   5        3          2          18  21
   6        3          3           7  25  26
   7        3          2          10  27
   8        3          2          21  23
   9        3          3          23  26  29
  10        3          3          13  14  21
  11        3          2          15  16
  12        3          2          17  26
  13        3          1          29
  14        3          1          15
  15        3          2          20  31
  16        3          2          19  20
  17        3          1          24
  18        3          3          19  22  25
  19        3          2          23  28
  20        3          1          22
  21        3          2          28  31
  22        3          2          28  30
  23        3          1          27
  24        3          2          25  31
  25        3          1          30
  26        3          1          27
  27        3          1          30
  28        3          1          29
  29        3          1          32
  30        3          1          32
  31        3          1          32
  32        1          0        
************************************************************************
REQUESTS/DURATIONS:
jobnr. mode duration  R 1  R 2  N 1  N 2
------------------------------------------------------------------------
  1      1     0       0    0    0    0
  2      1     4       9    5    7    8
         2     6       6    4    5    4
         3     9       4    2    5    2
  3      1     3       5    5    8   10
         2     7       5    5    6    9
         3     9       4    3    4    9
  4      1     3       8    7    3    4
         2     7       5    5    3    4
         3     8       2    1    2    4
  5      1     2       9    6    8   10
         2     9       9    5    6    5
         3    10       9    4    4    4
  6      1     1       9    5    7    6
         2     4       9    4    7    5
         3    10       8    3    6    5
  7      1     3       6    2    9    9
         2     5       5    1    7    5
         3    10       4    1    5    3
  8      1     2       9   10    6    2
         2     2       6   10    7    2
         3     8       5   10    3    2
  9      1     1       4    3    9    3
         2     2       3    3    8    3
         3    10       2    3    6    3
 10      1     2       7    3    4    5
         2     6       6    2    3    4
         3    10       5    2    2    2
 11      1     5       8    8   10    9
         2     9       8    7    9    7
         3    10       7    7    7    3
 12      1     4       3    7    6    8
         2     8       3    4    5    5
         3     8       3    3    3    6
 13      1     3       7    8    7    9
         2     5       5    8    6    5
         3     7       3    5    5    3
 14      1     3      10    8    6    3
         2     8       9    8    3    2
         3    10       9    8    1    2
 15      1     7       8    7    6    6
         2     7       6    7    7    7
         3    10       5    6    2    3
 16      1     4       6    9    8    8
         2     7       4    7    7    8
         3     8       2    5    7    7
 17      1     3       6    4    7    5
         2     5       5    3    5    4
         3     6       5    3    4    4
 18      1     2       2    7    5    8
         2     3       1    6    3    8
         3     9       1    5    1    7
 19      1     4      10    8    7   10
         2    10       6    5    7    7
         3    10       6    4    7    8
 20      1     4       8    4    9    5
         2    10       7    3    6    2
         3    10       8    1    6    4
 21      1     5       9    8    7    8
         2     5       8    7    7    9
         3    10       6    6    6    5
 22      1     1      10    6   10    9
         2     2       7    5   10    6
         3     5       6    3   10    1
 23      1     1       6    7    6    8
         2     2       5    6    6    6
         3     7       5    6    5    4
 24      1     1       2    9    3    8
         2     3       2    7    2    7
         3    10       2    6    2    7
 25      1     5       1    8    8    9
         2     8       1    6    7    7
         3     9       1    1    6    6
 26      1     3       5   10    5    5
         2     6       4    7    5    5
         3     9       2    7    5    4
 27      1     1       6    1    5    8
         2     4       4    1    5    8
         3    10       3    1    5    7
 28      1     3       6    7    9    6
         2     8       4    7    6    4
         3    10       2    5    1    1
 29      1     4       3    7    6    6
         2     5       3    6    4    5
         3     5       1    6    3    6
 30      1     1       8    5   10    6
         2     3       5    4    9    6
         3     9       5    4    8    6
 31      1     2       6    3    7    3
         2     6       3    3    5    3
         3     7       3    3    5    2
 32      1     0       0    0    0    0
************************************************************************
RESOURCEAVAILABILITIES:
  R 1  R 2  N 1  N 2
   26   29  192  186
************************************************************************