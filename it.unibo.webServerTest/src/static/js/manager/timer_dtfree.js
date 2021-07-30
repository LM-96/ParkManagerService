const timerdtfree_socket = new WebSocket(
    'ws://' +
    window.location.host +
    '/manager/timerdtfree/'
);

timerdtfree_socket.onmessage = function (e) {
    const data = JSON.parse(e.data);
    console.log(data)
}

