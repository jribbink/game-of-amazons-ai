export class Logger {
  lines: string[] = [];

  pushLines(lines: string[]) {
    var newlines: string[] = [];
    lines.forEach((line) => {
      newlines.push(...line.split(/\r\n|\r|\n/));
    });
    this.lines.unshift(...newlines);

    this.lines = this.lines.slice(0, Math.min(250, this.lines.length));
  }

  getLatest(): string[] {
    return this.lines;
  }
}
