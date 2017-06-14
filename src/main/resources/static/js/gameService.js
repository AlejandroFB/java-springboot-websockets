app.factory('gameService', function($rootScope) {
    var FIRST_PLAYER = 'First Player';
    var service      = {};

    service.updateBoard = updateBoard;

    return service;

    function updateBoard(msg) {
        var pitsOne = document.getElementsByClassName("row player-one")[0].children;
        var firstPlayerBigPit = document.getElementsByClassName("player-one store")[0];

        var pitsTwo = document.getElementsByClassName("row player-two")[0].children;
        var secondPlayerBigPit = document.getElementsByClassName("player-two store")[0];

        if ($rootScope.player == FIRST_PLAYER) {
            updateBoardFirstPlayer(pitsOne, pitsTwo, firstPlayerBigPit, secondPlayerBigPit, msg);
        } else {
            updateBoardSecondPlayer(pitsOne, pitsTwo, firstPlayerBigPit, secondPlayerBigPit, msg);
        }
    }

    function updateBoardFirstPlayer(pitsOne, pitsTwo, firstPlayerBigPit, secondPlayerBigPit, msg) {
        firstPlayerBigPit.textContent = msg.firstPlayerBigPit;
        secondPlayerBigPit.textContent = msg.secondPlayerBigPit;

        for (var i = 0; i < 6; i++) {
            pitsOne[i].textContent = msg.pitsFirstPlayer[i];
            pitsTwo[i].textContent = msg.pitsSecondPlayer[5-i];
        }
    }

    function updateBoardSecondPlayer(pitsOne, pitsTwo, firstPlayerBigPit, secondPlayerBigPit, msg) {
        secondPlayerBigPit.textContent = msg.firstPlayerBigPit;
        firstPlayerBigPit.textContent = msg.secondPlayerBigPit;

        for (var i = 0; i < 6; i++) {
            pitsTwo[i].textContent = msg.pitsFirstPlayer[5-i];
            pitsOne[i].textContent = msg.pitsSecondPlayer[i];
        }
    }
});