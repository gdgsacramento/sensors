
var io = require('socket.io').listen(8080);


io.configure(function () {

    io.set('log level', 1);
});

io.sockets.on('connection', function (socket) {

    console.log("DEBUG: socket.on.connection");

    socket.on('sensor', function (data) {

        console.log("######################### DEBUG: %j" , data);

        socket.broadcast.emit("foo", data);
    });
});

