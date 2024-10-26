-- Create a table to store each game session
CREATE TABLE games (
    id SERIAL PRIMARY KEY,           -- Unique game ID
    current_player CHAR(1),          -- 'X' or 'O', representing the current player's turn
    board VARCHAR(9),                -- A string representing the 3x3 game board ('---------', 'X--O--X--', etc.)
    status VARCHAR(10),              -- Game status ('ongoing', 'X_wins', 'O_wins', 'draw')
    created_at TIMESTAMP DEFAULT NOW()  -- Timestamp when the game was created
);

-- Create a table to track each move in the game
CREATE TABLE moves (
    id SERIAL PRIMARY KEY,           -- Unique move ID
    game_id INTEGER REFERENCES games(id),  -- The game this move is part of
    player CHAR(1),                  -- 'X' or 'O', representing the player making the move
    position INTEGER,                -- Position of the move on the board (0-8, representing the 9 cells)
    move_time TIMESTAMP DEFAULT NOW() -- Timestamp when the move was made
);
