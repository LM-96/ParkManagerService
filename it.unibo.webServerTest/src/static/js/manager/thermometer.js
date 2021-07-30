var status_thermometer = document.getElementById("status_thermometer");

const thermometer_socket = new WebSocket(
    'ws://' +
    window.location.host +
    '/manager/thermometer/'
);

thermometer_socket.onmessage = function (e) {
    const json_data = JSON.parse(e.data);
    console.log(json_data.data)
    const data = json_data.data
    status_thermometer.innerHTML = data;
}

