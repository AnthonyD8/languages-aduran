let gameId = null;
let currentPlayer = 'X';  // Start with Player X

// Function to start a new game
async function startNewGame() {
    const response = await fetch('/new-game', { method: 'POST' });
    const data = await response.json();
    gameId = data.gameId;
    currentPlayer = 'X';  // Reset to Player X at the start of a new game
    document.getElementById('game-status').innerText = data.message;
    updateBoard(data.board);
}

// Function to make a move
async function makeMove(index) {
    const cell = document.querySelector(`.cell[data-index='${index}']`);
    if (cell.innerText !== '' || !gameId) return;  // Ignore if already clicked or no game started

    // Make the move via the back-end API
    const response = await fetch('/move', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ gameId, position: index, player: currentPlayer })  // Send current player
    });
    const data = await response.json();

    // Update the board and status
    updateBoard(data.board);
    document.getElementById('game-status').innerText = data.status;

    // Switch players after the move
    if (data.status === 'ongoing') {
        currentPlayer = currentPlayer === 'X' ? 'O' : 'X';  // Switch players locally
    }
}

// Function to update the board
function updateBoard(board) {
    const cells = document.querySelectorAll('.cell');
    for (let i = 0; i < cells.length; i++) {
        cells[i].innerText = board[i] === '-' ? '' : board[i];  // Show X or O
    }
}

// Event listener for "Start New Game" button
document.getElementById('new-game-btn').addEventListener('click', startNewGame);

// Event listeners for each cell in the Tic-Tac-Toe grid
document.querySelectorAll('.cell').forEach(cell => {
    cell.addEventListener('click', (e) => {
        const index = e.target.getAttribute('data-index');
        makeMove(index);
    });
});
