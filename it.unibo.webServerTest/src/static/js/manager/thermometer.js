var status_thermometer = document.getElementById("status_thermometer");

const thermometer_socket = new WebSocket(
    'ws://' +
    window.location.host +
    '/manager/thermometer/'
);

thermometer_socket.onmessage = function (e) {
    console.log(e)

    const json_data = JSON.parse(e);
    console.log(json_data.data)

    status_thermometer.innerHTML = json_data.data;
}

