var app = angular
    .module('mancala', [])
    .run(run);

function run($rootScope, gameService) {
    var DOMAIN         = 'localhost:9000';
    var WEBSOCKET_PATH = 'ws://' + DOMAIN + '/mancala';

    // Game states
    var YOUR_TURN     = 'Your turn!';
    var ADD_TURN      = 'You have got an additional turn!';
    var OPPONENT_LEFT = 'Your opponent left the game.';
    var WAITING       = 'Looking for an opponent...';

    // Connect with server
    var socket = new WebSocket(WEBSOCKET_PATH);

    // Scope variables
    $rootScope.sowStones = sowStones;
    $rootScope.playerStatus = WAITING;

    socket.onmessage = function(event) {
        event instanceof MessageEvent;
        var msg = JSON.parse(event.data);

        if (msg.player !== null) {
            $rootScope.player = msg.player;
        } else if (!msg.turnMessage.includes(OPPONENT_LEFT)) {
            gameService.updateBoard(msg);
        }

        $rootScope.playerStatus = msg.turnMessage;
        $rootScope.$apply();
    };

    function sowStones(pitSelected) {
        var pitsOne = document.getElementsByClassName("row player-one")[0].children;
        var stones = pitsOne[pitSelected].textContent;

        var data = {pitSelected : pitSelected, stones : stones, player : $rootScope.player};

        if (stones > 0 && $rootScope.playerStatus == YOUR_TURN || $rootScope.playerStatus == ADD_TURN) {
            sendMessage(socket, data);
        }
    }

    function sendMessage(socket, data) {
        if (socket.readyState === WebSocket.OPEN) {
            socket.send(JSON.stringify(data));
            return true;
        }
        return false;
    }
}