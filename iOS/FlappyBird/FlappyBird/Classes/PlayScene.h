//
//  PlayScene.h
//  FlappyBird
//
//  Created by Hai Do Minh on 3/14/14.
//  Copyright 2014 Hai Do Minh. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "cocos2d.h"
#import "cocos2d-ui.h"
#import "Pipe.h"

@interface PlayScene : CCScene

+ (PlayScene *)scene;
- (id)init;

-(PipePosition) genPipePosition:(float)pipeH;
-(void)increaseScore;

//-(void) pauseGame;
//
//-(void) resumeGame;

@end
