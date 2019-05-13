# BunkOptimization
Bunk Optimization creates optimal bed arrangements in bunk/cabin for camps. It uses neighbor and bunk preferences of campers in evolutionary algorithm to estimate optimal solution. A GUI for specifying the bunk layout, seeing remaining time, and viewing the layout solution is included.

## User Instructions
After cloning/downloading repository or source code, follow "Set-up Instructions" then "Application Instructions." The user steps are summarized below.
1. Copy template of TemplateCSV
2. Fill out new .csv with correct info. 
    a. "x" for Top/Bottom Bunk Allowed indicates that level is allowed. 
    b. All other lines should be filled out with names or grade. The preferences can be left blank if wanted
3. Run BunkOptimization.exe
2. Select file:
	a. Browse through your files and select a .csv file you have entered data into
3. Sections in bunk:
	a. A section is defined as a continuous stretch of bunks.
	b. How many continuous stretches are in the bunk you are attempting to fill?
3. Bunks in each section:
	a. Enter the number of top and bottom bunks in each section of the bunk.
	b. The section numbers are as you define them.
4. Loops:
	a. This is the number of times the program runs.
	b. A higher number gives a better result, to a certain extent.
	c. 30 loops is considered a good result, but more is better if possible.
	d. The time for each loop is usually 1-2 minutes, but allow for up to 3 for large bunks/slow devices.
5. Time Remaining:
	a. The program is running, and you will get an output at the end.
6. Display:
	a. The bunk sections will be displayed separately.
	b. Top bunks are on top, and bottom bunks are on bottom.
	c. Occasionally, there will be multiple, equally satisfactory layouts.
	d. To switch between them, use the previous and next buttons.
	e. However, there is usually one arrangement.

## CSV Order
    Name Grade TopBunkAllowed BotBunkAllowed BunkmatePref1 BunkmatePref2 BunkmatePref3 NearPref1 NearPref2 NearPref3


## Important Methods: Descriptions
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
