Franklin van Nes - Find Nemo Game

The theme is an underwater theme based loosely off of Finding Nemo (though Patrick the starfish from Spongebob Squarepants is featured in the background). 
I thought an underwater theme would work nicely because it allows for some creative art and creative features.
For example, when you hit a turtle. The game speeds up temporarily and makes the user invincible to the fish hooks. In Finding Nemo,
he hitches a ride on some turtles which keep him safe and get him to his destination much quicker!
Also, starfishes work well as 'get' items being stars by their very nature.

The game was a fair challenge to code. I did encounter something strange in line in the while loop at line 85. The loop would never exit.
After some research on the KeyListener I found in the Grid class, I saw that it ran in a seperate thread, an Event listener thread. I thought perhaps there 
wasn't enough time to register and update the key before the while loop completes. I put in a print statement and it works! I would like to learn a more 
appropriate means of stalling a while loop, however.


-- ArtWork

Image of finding Nemo used under creative commons license. TM Disnet Pixar

I created the backround, turtle, fish hooks, starfish, and the user fish in Adobe Illustrator.
No other outside sources.