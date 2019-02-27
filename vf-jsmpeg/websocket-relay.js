// Use the websocket-relay to serve a raw MPEG-TS over WebSockets. You can use
// ffmpeg to feed the relay. ffmpeg -> websocket-relay -> browser
// Example:
// node websocket-relay yoursecret 8081 8082
// ffmpeg -i <some input> -f mpegts http://localhost:8081/yoursecret/ipcId

var fs = require('fs'),
    http = require('http'),
    WebSocket = require('ws');

if (process.argv.length < 3) {
    console.log(
        'Usage: \n' +
        'node websocket-relay.js <secret> [<stream-port> <websocket-port>]'
    );
    process.exit();
}

var STREAM_SECRET = process.argv[2],
    STREAM_PORT = process.argv[3] || 8081,
    WEBSOCKET_PORT = process.argv[4] || 8082,
    RECORD_STREAM = false;

var publishMap = new Map();

// Websocket Server
var socketServer = new WebSocket.Server({port: WEBSOCKET_PORT, perMessageDeflate: false});
socketServer.connectionCount = 0;
socketServer.on('connection', function (socket, upgradeReq) {
    var params = upgradeReq.url.substr(1).split('/');
    if (params.length < 2 || params[0] !== STREAM_SECRET) {
        console.log('Failed Websocket Connection, Bad Request Url: ' + upgradeReq.url);
        socket.close();
    }
    var ipcId = params[1]
    if (!publishMap.has(ipcId)) {
        console.log('Failed Websocket Connection, No Streem Available: ' + upgradeReq.url);
        socket.close();
    }
    socket.ipcId = ipcId
    socketServer.connectionCount++;
    console.log(
        'New WebSocket Connection: ',
        (upgradeReq || socket.upgradeReq).socket.remoteAddress,
        (upgradeReq || socket.upgradeReq).headers['user-agent'],
        '(' + socketServer.connectionCount + ' total)'
    );
    socket.on('close', function (code, message) {
        socketServer.connectionCount--;
        console.log(
            'Disconnected WebSocket (' + socketServer.connectionCount + ' total)'
        );
    });
});

socketServer.broadcast = function (ipcId, data) {
    socketServer.clients.forEach(function each(client) {
        if (client.readyState === WebSocket.OPEN && ipcId == client.ipcId) {
            client.send(data);
        }
    });
};


// HTTP Server to accept incomming MPEG-TS Stream from ffmpeg
var streamServer = http.createServer(function (request, response) {

    // 代理简单的静态资源服务
    if (request.url === "/view-stream.html") {
        fs.readFile('./view-stream.html', function (err, html) {
            if (err) {
                throw err;
            }
            response.writeHeader(200, {"Content-Type": "text/html"});
            response.write(html);
            response.end();
        });
        return;
    } else if (request.url === "/jsmpeg.min.js") {
        fs.readFile('./jsmpeg.min.js', function (err, js) {
            if (err) {
                throw err;
            }
            response.writeHeader(200, {"Content-Type": "text/javascript"});
            response.write(js);
            response.end();
        });
        return;
    }

    var params = request.url.substr(1).split('/');
    if (params[0] !== STREAM_SECRET) {
        console.log(
            'Failed Stream Connection: ' + request.socket.remoteAddress + ':' +
            request.socket.remotePort + ' - wrong secret.'
        );
        response.end();
    }
    if (params.length !== 2) {
        console.log(
            'Failed Stream Connection: ' + request.socket.remoteAddress + ':' +
            request.socket.remotePort + ' - no ipcid in url.'
        );
        response.end();
    }
    var ipcId = params[1]
    request.socket.ipcId = ipcId
    if (publishMap.has(ipcId)) {
        console.log(
            'Failed Stream Connection: ' + request.socket.remoteAddress + ':' +
            request.socket.remotePort + ' - url is already exists.'
        );
        response.end();
    } else {
        publishMap.set(ipcId, 0)
    }

    response.connection.setTimeout(0);
    console.log(
        'Stream Connected: ' +
        request.socket.remoteAddress + ':' +
        request.socket.remotePort + ":" + request.socket.ipcId
    );

    request.on('data', function (data) {
        socketServer.broadcast(ipcId, data);
        if (request.socket.recording) {
            request.socket.recording.write(data);
        }
    });

    request.on('end', function () {
        console.log('close');
        if (request.socket.recording) {
            request.socket.recording.close();
        }
        publishMap.delete(request.socket.ipcId)
    });

    // Record the stream to a local file?
    if (RECORD_STREAM) {
        var path = 'recordings/' + ipcId + "/" + Date.now() + '.ts';
        request.socket.recording = fs.createWriteStream(path);
    }
}).listen(STREAM_PORT);

console.log('Listening for incomming MPEG-TS Stream on http://127.0.0.1:' + STREAM_PORT + '/<secret>');
console.log('Awaiting WebSocket connections on ws://127.0.0.1:' + WEBSOCKET_PORT + '/');
