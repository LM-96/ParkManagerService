var status_outdoor = document.getElementById("status_oudoor");

const sonar_socket = new WebSocket(
    'ws://' +
    window.location.host +
    '/manager/sonar/'
);

sonar_socket.onmessage = function (e) {
    const data = JSON.parse(e.data);
    console.log(data)
    status_outdoor.innerHTML = data;
}
