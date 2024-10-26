const express = require('express');
const { Sequelize, DataTypes } = require('sequelize');
require('dotenv').config();

// Initialize Express
const app = express();
const port = process.env.PORT || 3000;
app.use(express.json());  // Middleware to parse JSON

// Serve static files from the "public" folder
app.use(express.static('public'));

// Initialize Sequelize (adjust the database connection as needed)
const sequelize = new Sequelize(process.env.DATABASE_URL);

// Define the Game model
const Game = sequelize.define('Game', {
    current_player: { type: DataTypes.CHAR(1), allowNull: false, defaultValue: 'X' },
    board: { type: DataTypes.STRING(9), allowNull: false, defaultValue: '---------' },
    status: { type: DataTypes.STRING(10), allowNull: false, defaultValue: 'ongoing' },
}, { timestamps: true });

// Define the Move model
const Move = sequelize.define('Move', {
    game_id: { type: DataTypes.INTEGER, allowNull: false, references: { model: Game, key: 'id' } },
    player: { type: DataTypes.CHAR(1), allowNull: false },
    position: { type: DataTypes.INTEGER, allowNull: false },
}, { timestamps: true });

// Sync models with the database
sequelize.sync();

// Helper function to check for a win
function checkWin(board, player) {
    const winPatterns = [
        [0, 1, 2], [3, 4, 5], [6, 7, 8],  // Rows
        [0, 3, 6], [1, 4, 7], [2, 5, 8],  // Columns
        [0, 4, 8], [2, 4, 6]              // Diagonals
    ];

    return winPatterns.some(pattern => 
        pattern.every(index => board[index] === player)
    );
}

// Route to start a new game
app.post('/new-game', async (req, res) => {
    try {
        const game = await Game.create({});
        res.status(201).json({ gameId: game.id, message: 'New game started!', board: game.board });
    } catch (error) {
        res.status(500).json({ error: 'Failed to start a new game' });
    }
});

// Route to make a move
app.post('/move', async (req, res) => {
    const { gameId, position, player } = req.body;

    try {
        const game = await Game.findByPk(gameId);
        if (!game || game.status !== 'ongoing') {
            return res.status(400).json({ error: 'Game not found or already finished' });
        }

        // Ensure the position is valid and the player is correct
        const boardArray = game.board.split('');
        if (boardArray[position] !== '-') {
            return res.status(400).json({ error: 'Position already taken' });
        }

        // Update the board
        boardArray[position] = player;
        game.board = boardArray.join('');

        // Check for a win or draw
        const isWin = checkWin(boardArray, player);
        if (isWin) {
            game.status = `${player}_wins`;
        } else if (!boardArray.includes('-')) {
            game.status = 'draw';
        } else {
            game.current_player = player === 'X' ? 'O' : 'X'; // Switch players
        }

        await game.save();
        await Move.create({ game_id: gameId, player, position });

        res.status(200).json({ board: game.board, status: game.status });
    } catch (error) {
        res.status(500).json({ error: 'Failed to make a move' });
    }
});

// Start the server
app.listen(port, () => {
    console.log(`-------------------------------------------`);
    console.log(`App is running and accessible at http://localhost:${port}`);
    console.log(`-------------------------------------------`);
});
