# Java Drawing Server

A client–server application in Java where several users connect over TCP to create and edit shared drawings made of geometric figures.

I built it as a university project at Universidad Rey Juan Carlos to practise object-oriented design and network programming with Java sockets and NIO.

## What it does

- **Figure model.** Points, circles, ellipses, squares, rectangles and groups of figures, all extending a common abstract `Figura` class. Figures can be moved and checked for whether they contain a given point.
- **Drawings with permissions.** Each drawing has an owner, and other users can be given view or edit rights. Drawings can be saved to and loaded from a text file.
- **Two servers:**
  - `red.echo`: a classic blocking server that starts one thread per client.
  - `red.nio`: a single-threaded, non-blocking server built on a `Selector`, able to handle many clients at once.
- **Custom application protocol.** When a client connects, it chooses between a binary format and JSON. Binary messages are little-endian: 1 byte for the message type, 4 bytes for a tag, 4 bytes for the length and then the UTF-8 content.
- **One service class per operation.** Each message type is handled by its own class in `servicios`, registered in a map, so a new operation can be added without touching the server loop.
- **Roles.** Only admins can create or delete drawings; any logged-in user can add, move, delete and list figures.

## Protocol

| Code | Message          | Content                              | Example                       |
|------|------------------|--------------------------------------|-------------------------------|
| 1    | `LOGIN`          | `<username> <admin\|user>`           | `ana admin`                   |
| 2    | `CREATE_DRAWING` | `<drawing>`                          | `Dibujo1`                     |
| 3    | `DELETE_DRAWING` | `<drawing>`                          | `Dibujo1`                     |
| 4    | `CREATE_FIGURE`  | `<drawing> <Type> <params>`          | `Dibujo1 Circulo 5 5 10`      |
| 5    | `DELETE_FIGURE`  | `<drawing> <figureId>`               | `Dibujo1 2`                   |
| 6    | `MOVE_FIGURE`    | `<drawing> <figureId> <dx> <dy>`     | `Dibujo1 1 3 3`               |
| 7    | `GET_FIGURES`    | `<drawing>`                          | `Dibujo1`                     |
| 100  | `RESPONSE`       | Server reply                         |                               |

Figure types (names in Spanish): `Punto x y`, `Circulo x y radius`, `Cuadrado x y side`, `Rectangulo x y width height`, `Elipse x y a b`.

## Example session

```
> 1  ana admin                     Usuario logueado: ana (admin)
> 2  Dibujo1                       Dibujo creado: Dibujo1
> 4  Dibujo1 Circulo 5 5 10        Figura agregada a Dibujo1
> 4  Dibujo1 Rectangulo 2 3 8 4    Figura agregada a Dibujo1
> 6  Dibujo1 1 3 3                 Figura movida en Dibujo1
> 5  Dibujo1 2                     Figura eliminada de Dibujo1
> 7  Dibujo1                       Dibujo:
                                   ID 1: Circulo 8 8 10
```

## Project structure

```
src/
├── figuras/      Figure classes (Punto, Circulo, Elipse, Cuadrado, Rectangulo, GrupoDeFiguras)
├── dibujos/      Drawing: figures by ID, permissions, save/load
├── usuarios/     Usuario, Administrador, DuenoDeDibujo
├── mensajes/     Protocol messages, binary encoding/decoding
├── servicios/    One service per message type
├── parsing/      Builds figures from text
├── red/echo/     Blocking echo server and client
├── red/nio/      Non-blocking NIO server and interactive client
└── main/         Small demos of the figure and drawing model
```

## How to run it

Requires Java 17 or later. The only external library is `org.json`, included in `lib/`.

**With IntelliJ IDEA:** open the folder, add `lib/json-20230227.jar` as a library (right click → *Add as Library*), run `red.nio.ServidorNIO` and then, in a second run, `red.nio.ClienteNIO`.

**From the terminal (Linux/macOS):**

```bash
javac -d out -cp "lib/*" $(find src -name "*.java" -not -path "src/test/*")
java -cp "out:lib/*" red.nio.ServidorNIO      # terminal 1
java -cp "out:lib/*" red.nio.ClienteNIO       # terminal 2
```

The server listens on port 12345. The client first asks which protocol to use (`BIN` or `JSON`) and then shows a menu with the operations above.

## Tech

Java · TCP sockets · Java NIO (`Selector`, `SocketChannel`) · multithreading · JSON (`org.json`)
