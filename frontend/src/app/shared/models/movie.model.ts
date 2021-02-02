import { Topic } from "./topic.model";

export class Movie {

    constructor(public id: string, public score: number, public topic: Topic) {}
}