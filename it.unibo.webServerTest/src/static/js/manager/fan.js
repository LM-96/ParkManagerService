var status_fan = document.getElementById("status_fan");

const fan_socket = new WebSocket(
    'ws://' +
    window.location.host +
    '/manager/fan/'
);


fan_socket.onmessage = function (e) {
    console.log(e.data)

    status_fan.innerHTML = e.data;
}

