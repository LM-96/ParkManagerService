const timerdtfree_socket = new WebSocket(
    'ws://' +
    window.location.host +
    '/manager/timerdtfree/'
);

timerdtfree_socket.onmessage = function (e) {
    console.log(e)

    const json_data = JSON.parse(e.data);
    console.log(json_data)

    const data = json_data.data
    //status_thermometer.innerHTML = data;
}

