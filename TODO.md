TODO
====

### create SpriteSwarm class 
has its own huge VBO (stream-style), each Sprite (or whatever we call it) member has a position_= function that either: 
- updates a swarm-wide array of positions, and when the swarm is rendered it uploads the entire array
- on swarm render we loop through all members and construct a new buffer to upload 
will probably want this for particle effects

# DT = 1 / FPS

# wtf colors wtf
# wtf acceleration wtf