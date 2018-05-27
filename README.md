# BunkOptimization
Creates optimal bed arrangements in bunk/cabin for camps. Uses neighbor and bunk preferences of campers in evolutionary algorithm to estimate optimal solution.

## User Interface
    Sections in Bunk: (Enter int)
    Bottom bunks in section 1: (Enter int)
    Top bunks in section 1: (Enter int)
    Bottom bunks in section 2: (Enter int)
    Top bunks in section 2: (Enter int)
etc...

## CSV Order
    Name Grade TopBunkAllowed BotBunkAllowed BunkmatePref1 BunkmatePref2 BunkmatePref3 NearPref1 NearPref2 NearPref3


## Methods
### Init Campers
From csv file, creates new **Camper** objects with properties: **name**, **grade**, **topBunkAllowed**, **botBunkAllowed**, 3 **bunkMatePref**, 3 **nearPref**
### Init Sections
Creates the number of sections in the bunk specified by user with the right number of top and bottom bunks in each
### Set Allowed Bunk Configs
Sets the number of allowed campers on wrong top and bottom bunks to minimize the number incorrect. This is the most important factor to adhere to, so it should be minimized.
### Init Population
Creates **INIT_POPULATION_SIZE** random permutations of arrangements
### Create Gen
Randomly shuffles 2 parents from SUS (discussed next) and assigns them to mate in PMX until **INIT_POPULATION_SIZE** offspring have been created
### SUS
Stochastic Universal Sampling. Calls **calPoints()** for every permutation in population. Checks to see if that is the best arrangement ever encountered. Randomly selects **KEEP_PERCENT** of the population to mate by running through set intervals and selecting the arrangement that falls within that cumulative point interval.
### Calc Points
Fitness function of algorithm. Assigns campers to sections. Per camper, adds **BUNK_LEVEL_ADDITION** points for being on an allowed bunk level, **BUNK_BUDDY_ADDITION** points for having a requested bunk buddy, **SIDE_ADDITION** points for having a near preference on the side, and **DIAGONAL_ADDITION** points for having a near preference on the diagonal.
### PMX
Partially-matched crossover. Uses two randomly-generated crossover points and swaps contents between parents. Maps changes and reorders points outside crossover points to prevent multiple of same camper in an arrangement.
### Arrangement Standard Deviation Filter
If there is tie between arrangements, the lowest-standard deviation one wins out. This is to minimize camper unhappiness with placement in the bunk.
### Print Format
Formats camper names in arrangement in way that user can understand
