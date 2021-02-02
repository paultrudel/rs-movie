import { Movie } from "./movie.model";
import { User } from "./user.model";

export class Review {

    constructor(
        public id: number, 
        public score: number, 
        public summary: string, 
        public content: string,
        public movie: Movie,
        public user: User 
    ) {}

}