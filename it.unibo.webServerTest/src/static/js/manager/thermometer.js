var status_thermometer = document.getElementById("status_thermometer");

const thermometer_socket = new WebSocket(
    'ws://' +
    window.location.host +
    '/manager/thermometer/'
);

thermometer_socket.onmessage = function (e) {
    const data = JSON.parse(e.data);
    console.log(data)
    status_thermometer.innerHTML = data;
}

