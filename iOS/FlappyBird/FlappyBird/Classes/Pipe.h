//
//  Pipe.h
//  FlappyBird
//
//  Created by Hai Do Minh on 3/21/14.
//  Copyright (c) 2014 Hai Do Minh. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "cocos2d.h"

typedef enum {
    PIPE_RED,
    PIPE_BLUE
} PipeType;

typedef struct PipePositon {
    float x;
    float top;
    float range;
} PipePosition;

@interface Pipe : NSObject
@property PipeType type;
@property CCSprite* sprTop;
@property CCSprite* sprBottom;
@property CCScene* scene;

-(id)initWithType:(PipeType)pType position:(PipePosition)position scene:(CCScene*)pScene birdPos:(float) pBirdX;
-(void) update:(CCTime)delta;
-(CGRect) getTopRect;
-(CGRect) getBottomRect;

@end
