# Twitter Simulation
This project simulates posting and retrieving tweets as the Twitter application would do.  It was built as a Java API with two current implementations using different databases, one with Postgres and one with Redis.  Read/write speeds were tested and documented for each implementation, then compared.

## To run: 
- run the 'generator' file in order to generate CSV files of random tweets and follower combinations
- Run the 'implementor' file to perform the simulation.  When running this file, follow the command line instructions to select a database type.

##  Project structure:
- generated CSV files are stored in the ./data directory
- results and instructions for both assignment versions are stored in the ./documents directory
