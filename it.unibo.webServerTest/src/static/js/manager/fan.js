var status_fan = document.getElementById("status_fan");

const fan_socket = new WebSocket(
    'ws://' +
    window.location.host +
    '/manager/fan/'
);


fan_socket.onmessage = function (e) {
    const json_data = JSON.parse(e.data);
    console.log(json_data.data)
    const data = json_data.data
    status_fan.innerHTML = data;
}

